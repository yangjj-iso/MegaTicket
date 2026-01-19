package com.megaticket.seat.controller;

import com.megaticket.common.result.Result;
import com.megaticket.seat.service.SeatService;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 座位控制器 - 项目核心 API
 * 提供高并发座位管理接口
 *
 * @author Yang JunJie
 * @since 2026/1/14
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/seat")
@RequiredArgsConstructor
@Validated
public class SeatController {

    private final SeatService seatService;

    /**
     * 锁定座位
     *
     * @param scheduleId      场次ID
     * @param seatPositions   座位位置列表 [{"row": 1, "col": 1}, {"row": 1, "col": 2}]
     * @return 锁定成功的座位列表
     */
    @PostMapping("/lock")
    public Result<List<Map<String, Integer>>> lockSeats(
            @RequestParam("scheduleId")
            @NotNull(message = "场次ID不能为空")
            @Positive(message = "场次ID必须为正数")
            Long scheduleId,

            @RequestBody
            @NotNull(message = "座位列表不能为空")
            List<Map<String, Integer>> seatPositions) {

        return Result.success(seatService.lockSeats(scheduleId, seatPositions));
    }

    /**
     * 释放座位
     *
     * @param scheduleId      场次ID
     * @param seatPositions   座位位置列表
     * @return 释放成功的座位数量
     */
    @PostMapping("/release")
    public Result<Integer> releaseSeats(
            @RequestParam("scheduleId")
            @NotNull(message = "场次ID不能为空")
            @Positive(message = "场次ID必须为正数")
            Long scheduleId,

            @RequestBody
            @NotNull(message = "座位列表不能为空")
            List<Map<String, Integer>> seatPositions) {

        return Result.success(seatService.releaseSeats(scheduleId, seatPositions));
    }

    /**
     * 标记座位已售出
     *
     * @param scheduleId      场次ID
     * @param seatPositions   座位位置列表
     * @return 售出的座位数量
     */
    @PostMapping("/sold")
    public Result<Integer> markSeatsSold(
            @RequestParam("scheduleId")
            @NotNull(message = "场次ID不能为空")
            @Positive(message = "场次ID必须为正数")
            Long scheduleId,

            @RequestBody
            @NotNull(message = "座位列表不能为空")
            List<Map<String, Integer>> seatPositions) {

        return Result.success(seatService.markSeatsSold(scheduleId, seatPositions));
    }

    /**
     * 获取座位状态图
     *
     * @param scheduleId 场次ID
     * @param rowStart   起始行
     * @param rowEnd     结束行
     * @param colStart   起始列
     * @param colEnd     结束列
     * @return 座位状态图 {row: {col: status}}
     *
     * 座位状态: 0=可选, 1=已锁定, 2=已售出
     */
    @GetMapping("/status")
    public Result<Map<Integer, Map<Integer, Integer>>> getSeatStatus(
            @RequestParam("scheduleId")
            @NotNull(message = "场次ID不能为空")
            @Positive(message = "场次ID必须为正数")
            Long scheduleId,

            @RequestParam(value = "rowStart", defaultValue = "1")
            @Min(value = 1, message = "起始行至少为1")
            @Max(value = 50, message = "起始行最多为50")
            Integer rowStart,

            @RequestParam(value = "rowEnd", defaultValue = "15")
            @Min(value = 1, message = "结束行至少为1")
            @Max(value = 50, message = "结束行最多为50")
            Integer rowEnd,

            @RequestParam(value = "colStart", defaultValue = "1")
            @Min(value = 1, message = "起始列至少为1")
            @Max(value = 100, message = "起始列最多为100")
            Integer colStart,

            @RequestParam(value = "colEnd", defaultValue = "20")
            @Min(value = 1, message = "结束列至少为1")
            @Max(value = 100, message = "结束列最多为100")
            Integer colEnd) {

        return Result.success(seatService.getSeatStatus(
            scheduleId, rowStart, rowEnd, colStart, colEnd
        ));
    }

    /**
     * 初始化场次座位图（用于排期发布时调用）
     *
     * @param scheduleId 场次ID
     * @param totalRows  总行数
     * @param totalCols  总列数
     * @return 操作结果
     */
    @PostMapping("/init")
    public Result<Void> initSeatMap(
            @RequestParam("scheduleId")
            @NotNull(message = "场次ID不能为空")
            @Positive(message = "场次ID必须为正数")
            Long scheduleId,

            @RequestParam("totalRows")
            @NotNull(message = "总行数不能为空")
            @Min(value = 1, message = "总行数至少为1")
            @Max(value = 50, message = "总行数最多为50")
            Integer totalRows,

            @RequestParam("totalCols")
            @NotNull(message = "总列数不能为空")
            @Min(value = 1, message = "总列数至少为1")
            @Max(value = 100, message = "总列数最多为100")
            Integer totalCols) {

        seatService.initSeatMap(scheduleId, totalRows, totalCols);
        return Result.success();
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("Seat service is running");
    }
}
