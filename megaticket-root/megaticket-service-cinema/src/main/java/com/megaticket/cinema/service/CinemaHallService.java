package com.megaticket.cinema.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.megaticket.common.exception.BusinessException;
import com.megaticket.common.result.ResultCode;
import com.megaticket.cinema.entity.CinemaHall;
import com.megaticket.cinema.mapper.CinemaHallMapper;
import com.megaticket.cinema.mapper.CinemaMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 影院厅服务类
 * author Yang JunJie
 * since 2026/1/14
 * version 1.0
 */
@Slf4j
@Service
@AllArgsConstructor
public class CinemaHallService {

    // 影院厅Mapper
    private final CinemaHallMapper cinemaHallMapper;
    // 影院Mapper（用于校验影院是否存在）
    private final CinemaMapper cinemaMapper;

    /**
     * 根据影院ID获取影厅列表
     * @param cinemaId 影院ID
     * @return 影厅列表
     */
    public List<CinemaHall> getHallsByCinemaId(Long cinemaId) {
        // 1. 参数校验
        if (cinemaId == null || cinemaId <= 0) {
            log.warn("影院ID参数无效: {}", cinemaId);
            throw new BusinessException(ResultCode.CINEMA_NOT_FOUND);
        }

        // 2. 校验影院是否存在
        if (cinemaMapper.selectById(cinemaId) == null) {
            log.warn("影院不存在, cinemaId={}", cinemaId);
            throw new BusinessException(ResultCode.CINEMA_NOT_FOUND);
        }

        try {
            // 3. 查询影厅列表
            QueryWrapper<CinemaHall> qw = new QueryWrapper<>();
            qw.eq("cinema_id", cinemaId);
            List<CinemaHall> halls = cinemaHallMapper.selectList(qw);

            if (halls == null) {
                log.warn("查询影厅列表返回null, cinemaId={}", cinemaId);
                return Collections.emptyList();
            }

            log.info("根据影院ID查询影厅成功，cinemaId={}, 共{}条", cinemaId, halls.size());
            return halls;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据影院ID查询影厅失败, cinemaId={}", cinemaId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    /**
     * 获取影厅详情
     * @param hallId 影厅ID
     * @return 影厅详情
     */
    public CinemaHall getHallDetail(Long hallId) {
        // 1. 参数校验
        if (hallId == null || hallId <= 0) {
            log.warn("影厅ID参数无效: {}", hallId);
            throw new BusinessException(ResultCode.CINEMA_HALL_NOT_FOUND);
        }

        try {
            // 2. 查询影厅详情
            CinemaHall hall = cinemaHallMapper.selectById(hallId);

            if (hall == null) {
                log.warn("影厅不存在, hallId={}", hallId);
                throw new BusinessException(ResultCode.CINEMA_HALL_NOT_FOUND);
            }

            log.info("获取影厅详情成功, hallId={}", hallId);
            return hall;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取影厅详情失败, hallId={}", hallId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    /**
     * 根据影院ID和影厅名称获取影厅
     * @param cinemaId 影院ID
     * @param hallName 影厅名称
     * @return 影厅列表
     */
    public List<CinemaHall> getHallsByCinemaIdAndName(Long cinemaId, String hallName) {
        // 1. 参数校验
        if (cinemaId == null || cinemaId <= 0) {
            log.warn("影院ID参数无效: {}", cinemaId);
            throw new BusinessException(ResultCode.CINEMA_NOT_FOUND);
        }

        if (!StringUtils.hasText(hallName)) {
            log.warn("影厅名称参数为空");
            throw new BusinessException(ResultCode.CINEMA_HALL_NAME_INVALID);
        }

        // 2. 校验影院是否存在
        if (cinemaMapper.selectById(cinemaId) == null) {
            log.warn("影院不存在, cinemaId={}", cinemaId);
            throw new BusinessException(ResultCode.CINEMA_NOT_FOUND);
        }

        try {
            // 3. 查询影厅列表
            QueryWrapper<CinemaHall> qw = new QueryWrapper<>();
            qw.eq("cinema_id", cinemaId);
            qw.like("name", hallName);
            List<CinemaHall> halls = cinemaHallMapper.selectList(qw);

            if (halls == null) {
                log.warn("查询影厅列表返回null, cinemaId={}, hallName={}", cinemaId, hallName);
                return Collections.emptyList();
            }

            log.info("根据影院ID和名称查询影厅成功，cinemaId={}, hallName={}, 共{}条", cinemaId, hallName, halls.size());
            return halls;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据影院ID和名称查询影厅失败, cinemaId={}, hallName={}", cinemaId, hallName, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    /**
     * 创建影厅
     * @param cinemaHall 影厅信息
     * @return 创建的影厅ID
     */
    public Long createHall(CinemaHall cinemaHall) {
        // 1. 参数校验
        if (cinemaHall == null) {
            log.warn("影厅信息为空");
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR);
        }

        if (cinemaHall.getCinemaId() == null || cinemaHall.getCinemaId() <= 0) {
            log.warn("影院ID无效: {}", cinemaHall.getCinemaId());
            throw new BusinessException(ResultCode.CINEMA_NOT_FOUND);
        }

        // 2. 校验影院是否存在
        if (cinemaMapper.selectById(cinemaHall.getCinemaId()) == null) {
            log.warn("影院不存在, cinemaId={}", cinemaHall.getCinemaId());
            throw new BusinessException(ResultCode.CINEMA_NOT_FOUND);
        }

        // 3. 校验影厅名称
        if (!StringUtils.hasText(cinemaHall.getName())) {
            log.warn("影厅名称为空");
            throw new BusinessException(ResultCode.CINEMA_HALL_NAME_INVALID);
        }

        // 4. 校验行列数
        if (cinemaHall.getTotalRows() == null || cinemaHall.getTotalRows() < 1 || cinemaHall.getTotalRows() > 50) {
            log.warn("影厅行数无效: {}", cinemaHall.getTotalRows());
            throw new BusinessException(ResultCode.CINEMA_HALL_ROWS_INVALID);
        }

        if (cinemaHall.getTotalCols() == null || cinemaHall.getTotalCols() < 1 || cinemaHall.getTotalCols() > 100) {
            log.warn("影厅列数无效: {}", cinemaHall.getTotalCols());
            throw new BusinessException(ResultCode.CINEMA_HALL_COLS_INVALID);
        }

        try {
            // 5. 插入影厅
            int result = cinemaHallMapper.insert(cinemaHall);

            if (result <= 0) {
                log.error("创建影厅失败");
                throw new BusinessException(ResultCode.SYSTEM_ERROR);
            }

            log.info("创建影厅成功, hallId={}", cinemaHall.getId());
            return cinemaHall.getId();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("创建影厅失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    /**
     * 更新影厅信息
     * @param cinemaHall 影厅信息
     * @return 是否更新成功
     */
    public Boolean updateHall(CinemaHall cinemaHall) {
        // 1. 参数校验
        if (cinemaHall == null || cinemaHall.getId() == null || cinemaHall.getId() <= 0) {
            log.warn("影厅ID无效");
            throw new BusinessException(ResultCode.CINEMA_HALL_NOT_FOUND);
        }

        // 2. 校验影厅是否存在
        CinemaHall existingHall = cinemaHallMapper.selectById(cinemaHall.getId());
        if (existingHall == null) {
            log.warn("影厅不存在, hallId={}", cinemaHall.getId());
            throw new BusinessException(ResultCode.CINEMA_HALL_NOT_FOUND);
        }

        try {
            // 3. 更新影厅
            int result = cinemaHallMapper.updateById(cinemaHall);

            if (result <= 0) {
                log.error("更新影厅失败");
                throw new BusinessException(ResultCode.SYSTEM_ERROR);
            }

            log.info("更新影厅成功, hallId={}", cinemaHall.getId());
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("更新影厅失败, hallId={}", cinemaHall.getId(), e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    /**
     * 删除影厅（逻辑删除）
     * @param hallId 影厅ID
     * @return 是否删除成功
     */
    public Boolean deleteHall(Long hallId) {
        // 1. 参数校验
        if (hallId == null || hallId <= 0) {
            log.warn("影厅ID参数无效: {}", hallId);
            throw new BusinessException(ResultCode.CINEMA_HALL_NOT_FOUND);
        }

        // 2. 校验影厅是否存在
        CinemaHall existingHall = cinemaHallMapper.selectById(hallId);
        if (existingHall == null) {
            log.warn("影厅不存在, hallId={}", hallId);
            throw new BusinessException(ResultCode.CINEMA_HALL_NOT_FOUND);
        }

        try {
            // 3. 逻辑删除影厅
            int result = cinemaHallMapper.deleteById(hallId);

            if (result <= 0) {
                log.error("删除影厅失败");
                throw new BusinessException(ResultCode.SYSTEM_ERROR);
            }

            log.info("删除影厅成功, hallId={}", hallId);
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("删除影厅失败, hallId={}", hallId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }
}
