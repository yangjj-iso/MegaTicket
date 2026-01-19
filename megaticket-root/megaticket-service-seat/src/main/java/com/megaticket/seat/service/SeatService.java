package com.megaticket.seat.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.megaticket.common.constant.RedisKeyConstant;
import com.megaticket.common.exception.BusinessException;
import com.megaticket.common.result.ResultCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 座位服务 - 项目核心
 * 使用 Redis Bitfield + Lua 脚本实现高并发座位管理
 *
 * @author Yang JunJie
 * @since 2026/1/14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeatService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private String lockSeatLuaScript;
    private DefaultRedisScript<String> redisScript;
    private static final String LUA_SCRIPT_PATH = "lua/lock_seat.lua";
    private static final int LOCK_TIMEOUT_SECONDS = 900; // 15分钟锁定时间

    /**
     * 初始化时加载 Lua 脚本
     */
    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource(LUA_SCRIPT_PATH);
            lockSeatLuaScript = new String(resource.getContentAsByteArray(), StandardCharsets.UTF_8);
            
            redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(lockSeatLuaScript);
            redisScript.setResultType(String.class);
            
            log.info("座位锁定 Lua 脚本加载成功");
        } catch (IOException e) {
            log.error("加载 Lua 脚本失败: {}", LUA_SCRIPT_PATH, e);
            throw new RuntimeException("加载 Lua 脚本失败", e);
        }
    }

    /**
     * 锁定座位
     *
     * @param scheduleId  场次ID
     * @param seatPositions 座位位置列表 [{row, col}, ...]
     * @return 锁定成功的座位列表
     */
    public List<Map<String, Integer>> lockSeats(Long scheduleId, List<Map<String, Integer>> seatPositions) {
        // 1. 参数校验
        if (scheduleId == null || scheduleId <= 0) {
            throw new BusinessException(ResultCode.SCHEDULE_NOT_FOUND);
        }

        if (seatPositions == null || seatPositions.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR);
        }

        // 2. 检查场次是否存在
        String seatMapKey = RedisKeyConstant.SEAT_MAP_PREFIX + scheduleId;
        Boolean exists = redisTemplate.hasKey(seatMapKey);
        if (Boolean.FALSE.equals(exists)) {
            throw new BusinessException(ResultCode.SCHEDULE_NOT_FOUND);
        }

        // 3. 校验每个座位的行列号
        for (Map<String, Integer> seat : seatPositions) {
            Integer row = seat.get("row");
            Integer col = seat.get("col");

            if (row == null || row < 1 || row > 50) {
                throw new BusinessException(ResultCode.SEAT_INVALID_ROW);
            }
            if (col == null || col < 1 || col > 100) {
                throw new BusinessException(ResultCode.SEAT_INVALID_COL);
            }
        }

        try {
            // 4. 构建 Lua 脚本参数
            List<String> keys = List.of(seatMapKey);
            List<Object> args = new ArrayList<>();

            args.add(LOCK_TIMEOUT_SECONDS);        // ARGV[1]: 锁定超时时间
            args.add(System.currentTimeMillis() / 1000);  // ARGV[2]: 当前时间戳
            args.add(seatPositions.size());        // ARGV[3]: 座位数量

            // ARGV[4...]: 座位位置 (row1, col1, row2, col2, ...)
            for (Map<String, Integer> seat : seatPositions) {
                args.add(seat.get("row"));
                args.add(seat.get("col"));
            }
            args.add("lock");                      // 最后一个参数: 操作类型

            // 5. 执行 Lua 脚本
            String result = redisTemplate.execute(
                redisScript,
                keys,
                args.toArray()
            );

            // 6. 解析结果
            if (result == null) {
                throw new BusinessException(ResultCode.SEAT_STATUS_QUERY_FAILED);
            }

            String jsonResult = result;
            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = objectMapper.readValue(jsonResult, new TypeReference<>() {});

            Boolean success = (Boolean) resultMap.get("success");

            if (Boolean.FALSE.equals(success)) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> failedSeats = (List<Map<String, Object>>) resultMap.get("failed_seats");

                if (failedSeats != null && !failedSeats.isEmpty()) {
                    Map<String, Object> firstFailed = failedSeats.get(0);
                    String reason = (String) firstFailed.get("reason");

                    if ("sold_out".equals(reason)) {
                        throw new BusinessException(ResultCode.SEAT_SOLD_OUT);
                    } else if ("locked".equals(reason)) {
                        throw new BusinessException(ResultCode.SEAT_ALREADY_LOCKED);
                    }
                }
                throw new BusinessException(ResultCode.SEAT_ALREADY_LOCKED);
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> lockedSeats = (List<Map<String, Object>>) resultMap.get("locked_seats");
            List<Map<String, Integer>> resultSeats = new ArrayList<>();

            if (lockedSeats != null) {
                for (Map<String, Object> seat : lockedSeats) {
                    Map<String, Integer> seatInfo = new HashMap<>();
                    seatInfo.put("row", ((Number) seat.get("row")).intValue());
                    seatInfo.put("col", ((Number) seat.get("col")).intValue());
                    resultSeats.add(seatInfo);
                }
            }

            log.info("锁定座位成功, scheduleId={}, seats={}", scheduleId, resultSeats.size());
            return resultSeats;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("锁定座位失败, scheduleId={}", scheduleId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    /**
     * 释放座位
     *
     * @param scheduleId  场次ID
     * @param seatPositions 座位位置列表
     * @return 释放成功的座位数量
     */
    public Integer releaseSeats(Long scheduleId, List<Map<String, Integer>> seatPositions) {
        // 1. 参数校验
        if (scheduleId == null || scheduleId <= 0) {
            throw new BusinessException(ResultCode.SCHEDULE_NOT_FOUND);
        }

        if (seatPositions == null || seatPositions.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR);
        }

        String seatMapKey = RedisKeyConstant.SEAT_MAP_PREFIX + scheduleId;

        try {
            // 2. 构建 Lua 脚本参数
            List<String> keys = List.of(seatMapKey);
            List<Object> args = new ArrayList<>();

            args.add(System.currentTimeMillis() / 1000);  // ARGV[1]: 当前时间戳
            args.add(seatPositions.size());               // ARGV[2]: 座位数量

            // ARGV[3...]: 座位位置
            for (Map<String, Integer> seat : seatPositions) {
                args.add(seat.get("row"));
                args.add(seat.get("col"));
            }
            args.add("release");                         // 操作类型

            // 3. 执行 Lua 脚本
            String result = redisTemplate.execute(
                redisScript,
                keys,
                args.toArray()
            );

            if (result == null) {
                throw new BusinessException(ResultCode.SEAT_RELEASE_FAILED);
            }

            Integer releasedCount = Integer.parseInt(result);
            log.info("释放座位成功, scheduleId={}, count={}", scheduleId, releasedCount);

            return releasedCount;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("释放座位失败, scheduleId={}", scheduleId, e);
            throw new BusinessException(ResultCode.SEAT_RELEASE_FAILED);
        }
    }

    /**
     * 标记座位已售出
     *
     * @param scheduleId  场次ID
     * @param seatPositions 座位位置列表
     * @return 售出的座位数量
     */
    public Integer markSeatsSold(Long scheduleId, List<Map<String, Integer>> seatPositions) {
        // 1. 参数校验
        if (scheduleId == null || scheduleId <= 0) {
            throw new BusinessException(ResultCode.SCHEDULE_NOT_FOUND);
        }

        if (seatPositions == null || seatPositions.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR);
        }

        String seatMapKey = RedisKeyConstant.SEAT_MAP_PREFIX + scheduleId;

        try {
            // 2. 构建 Lua 脚本参数
            List<String> keys = List.of(seatMapKey);
            List<Object> args = new ArrayList<>();

            args.add(seatPositions.size());  // ARGV[1]: 座位数量

            // ARGV[2...]: 座位位置
            for (Map<String, Integer> seat : seatPositions) {
                args.add(seat.get("row"));
                args.add(seat.get("col"));
            }
            args.add("sold");  // 操作类型

            // 3. 执行 Lua 脚本
            String result = redisTemplate.execute(
                redisScript,
                keys,
                args.toArray()
            );

            if (result == null) {
                throw new BusinessException(ResultCode.SEAT_SOLD_FAILED);
            }

            Integer soldCount = Integer.parseInt(result);
            log.info("标记座位已售出, scheduleId={}, count={}", scheduleId, soldCount);

            return soldCount;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("标记座位已售出失败, scheduleId={}", scheduleId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    /**
     * 获取座位状态图
     *
     * @param scheduleId  场次ID
     * @param rowStart    起始行
     * @param rowEnd      结束行
     * @param colStart    起始列
     * @param colEnd      结束列
     * @return 座位状态图 {row: {col: status}}
     */
    public Map<Integer, Map<Integer, Integer>> getSeatStatus(
            Long scheduleId,
            Integer rowStart,
            Integer rowEnd,
            Integer colStart,
            Integer colEnd) {

        // 1. 参数校验
        if (scheduleId == null || scheduleId <= 0) {
            throw new BusinessException(ResultCode.SCHEDULE_NOT_FOUND);
        }

        if (rowStart == null || rowStart < 1 || rowStart > 50 ||
            rowEnd == null || rowEnd < rowStart || rowEnd > 50 ||
            colStart == null || colStart < 1 || colStart > 100 ||
            colEnd == null || colEnd < colStart || colEnd > 100) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR);
        }

        String seatMapKey = RedisKeyConstant.SEAT_MAP_PREFIX + scheduleId;

        try {
            // 2. 构建 Lua 脚本参数
            List<String> keys = List.of(seatMapKey);
            List<Object> args = new ArrayList<>();

            args.add(rowStart);
            args.add(rowEnd);
            args.add(colStart);
            args.add(colEnd);
            args.add("status");                       // 操作类型

            // 3. 执行 Lua 脚本
            String result = redisTemplate.execute(
                redisScript,
                keys,
                args.toArray()
            );

            if (result == null) {
                throw new BusinessException(ResultCode.SEAT_STATUS_QUERY_FAILED);
            }

            // 4. 解析结果
            String jsonResult = result;
            return objectMapper.readValue(jsonResult, new TypeReference<>() {});

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取座位状态失败, scheduleId={}", scheduleId, e);
            throw new BusinessException(ResultCode.SEAT_STATUS_QUERY_FAILED);
        }
    }

    /**
     * 初始化场次座位图
     *
     * @param scheduleId  场次ID
     * @param totalRows   总行数
     * @param totalCols   总列数
     */
    public void initSeatMap(Long scheduleId, Integer totalRows, Integer totalCols) {
        if (scheduleId == null || scheduleId <= 0) {
            throw new BusinessException(ResultCode.SCHEDULE_NOT_FOUND);
        }

        if (totalRows == null || totalRows < 1 || totalRows > 50 ||
            totalCols == null || totalCols < 1 || totalCols > 100) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR);
        }

        String seatMapKey = RedisKeyConstant.SEAT_MAP_PREFIX + scheduleId;

        try {
            // 初始化 Bitfield，所有座位设为 0（可选）
            // Redis 会自动处理，访问不存在的位时返回 0
            log.info("初始化场次座位图, scheduleId={}, rows={}, cols={}", scheduleId, totalRows, totalCols);

        } catch (Exception e) {
            log.error("初始化场次座位图失败, scheduleId={}", scheduleId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }
}
