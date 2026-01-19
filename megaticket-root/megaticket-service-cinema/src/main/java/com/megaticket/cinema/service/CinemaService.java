package com.megaticket.cinema.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.megaticket.common.exception.BusinessException;
import com.megaticket.common.result.ResultCode;
import com.megaticket.cinema.entity.Cinema;
import com.megaticket.cinema.mapper.CinemaMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 影院服务类
 * author Yang JunJie
 * since 2026/1/14
 * version 1.0
 */
@Slf4j
@Service
@AllArgsConstructor
public class CinemaService {

    // 影院Mapper
    private final CinemaMapper cinemaMapper;

    /**
     * 获取所有影院列表
     * @return 影院列表
     */
    public List<Cinema> getAllCinemas() {
        try {
            // 1. 查询所有影院
            List<Cinema> cinemas = cinemaMapper.selectList(null);

            // 2. 处理查询结果
            if (cinemas == null) {
                log.warn("查询影院列表返回null");
                return Collections.emptyList();
            }
            log.info("获取影院列表成功，共{}条", cinemas.size());
            return cinemas;
        } catch (Exception e) {
            log.error("获取影院列表失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    /**
     * 按照名称模糊查询影院列表
     * @param name 影院名称关键词
     * @return 影院列表
     */
    public List<Cinema> getCinemasByLocation(String name) {
        // 1. 参数校验
        if (!StringUtils.hasText(name)) {
            log.warn("影院名称参数为空");
            throw new BusinessException(ResultCode.CINEMA_NAME_INVALID);
        }

        try {
            // 2. 构建查询条件并执行查询
            QueryWrapper<Cinema> qw = new QueryWrapper<>();
            qw.like("name", name);
            List<Cinema> cinemas = cinemaMapper.selectList(qw);

            // 3. 处理查询结果
            if (cinemas == null) {
                log.warn("根据名称查询影院列表返回null, name={}", name);
                return Collections.emptyList();
            }

            log.info("根据名称查询影院成功，name={}, 共{}条", name, cinemas.size());
            return cinemas;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据名称查询影院失败, name={}", name, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    /**
     * 根据城市代码获取影院列表
     * @param cityCode 城市代码
     * @return 影院列表
     */
    public List<Cinema> getCinemasByCityCode(String cityCode) {
        // 1. 参数校验
        if (!StringUtils.hasText(cityCode)) {
            log.warn("城市代码参数为空");
            throw new BusinessException(ResultCode.CINEMA_CITY_CODE_INVALID);
        }

        // 2. 简单校验城市代码格式 (假设为6位数字)
        if (!cityCode.matches("^[0-9]{6}$")) {
            log.warn("城市代码格式无效: {}", cityCode);
            throw new BusinessException(ResultCode.CINEMA_CITY_CODE_INVALID);
        }

        try {
            // 3. 构建查询条件并执行查询
            QueryWrapper<Cinema> qw = new QueryWrapper<>();
            qw.eq("city_code", cityCode);
            List<Cinema> cinemas = cinemaMapper.selectList(qw);

            // 4. 处理查询结果
            if (cinemas == null) {
                log.warn("根据城市代码查询影院列表返回null, cityCode={}", cityCode);
                return Collections.emptyList();
            }

            log.info("根据城市代码查询影院成功，cityCode={}, 共{}条", cityCode, cinemas.size());
            return cinemas;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("根据城市代码查询影院失败, cityCode={}", cityCode, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    /**
     * 获取影院详情
     * @param cinemaId 影院ID
     * @return 影院详情
     */
    public Cinema getCinemaDetail(Long cinemaId) {
        // 1. 参数校验
        if (cinemaId == null || cinemaId <= 0) {
            log.warn("影院ID参数无效: {}", cinemaId);
            throw new BusinessException(ResultCode.CINEMA_NOT_FOUND);
        }

        try {
            // 2. 查询影院详情
            Cinema cinema = cinemaMapper.selectById(cinemaId);

            // 3. 处理查询结果
            if (cinema == null) {
                log.warn("影院不存在, cinemaId={}", cinemaId);
                throw new BusinessException(ResultCode.CINEMA_NOT_FOUND);
            }

            log.info("获取影院详情成功, cinemaId={}", cinemaId);
            return cinema;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取影院详情失败, cinemaId={}", cinemaId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }
}
