package com.megaticket.cinema.controller;

import com.megaticket.cinema.entity.Cinema;
import com.megaticket.cinema.service.CinemaService;
import com.megaticket.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * 影院控制器
 * author Yang JunJie
 * since 2026/1/14
 * version 1.0
 */
@RestController
@RequestMapping("/api/v1/cinema")
@RequiredArgsConstructor
public class CinemaController {

    // 影院服务(构造器注入)
    private final CinemaService cinemaService;

    /**
     * 获取影院列表(全部)
     * @return 影院列表
     */
    @RequestMapping("/list")
    public Result<List<Cinema>> getCinemaList()
    {
        return Result.success(cinemaService.getAllCinemas());
    }

    /**
      获取影院列表(地点模糊查询)
      @param name 地点
     * @return 影院列表
     */
    @RequestMapping("/list-by-name")
    public Result<List<Cinema>> getCinemasByName(String name)
    {
        return null;
    }

    /**
     * 根据城市代码获取影院列表
     * @param cityCode 城市代码
     * @return 影院列表
     */
    @RequestMapping("/list-by-city")
    public Result<List<Cinema>> getCinemaByCityCode(String cityCode)
    {
        return null;
    }

    /**
     * 获取影院详情
     * @param cinemaId 影院ID
     * @return 影院详情
     */
    @RequestMapping("/detail")
    public Result<Cinema> getCinemaDetail(Long cinemaId)
    {
        return null;
    }

}
