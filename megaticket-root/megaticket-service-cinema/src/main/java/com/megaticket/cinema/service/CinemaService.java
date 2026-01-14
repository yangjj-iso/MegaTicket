package com.megaticket.cinema.service;

import com.megaticket.cinema.entity.Cinema;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.megaticket.cinema.mapper.CinemaMapper;

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
        return cinemaMapper.selectList(null);
    }
}
