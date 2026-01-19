package com.megaticket.cinema.controller;

import com.megaticket.cinema.entity.Cinema;
import com.megaticket.cinema.service.CinemaService;
import com.megaticket.common.result.Result;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
@Validated
public class CinemaController {

    // 影院服务(构造器注入)
    private final CinemaService cinemaService;

    /**
     * 获取影院列表(全部)
     * @return 影院列表
     */
    @GetMapping("/list")
    public Result<List<Cinema>> getCinemaList()
    {
        return Result.success(cinemaService.getAllCinemas());
    }

    /**
      获取影院列表(地点模糊查询)
      @param name 影院名称
     * @return 影院列表
     */
    @GetMapping("/list-by-name/{name}")
    public Result<List<Cinema>> getCinemasByLocation(
            @PathVariable("name")
            @NotBlank(message = "影院名称不能为空")
            @Size(min = 1, max = 50, message = "影院名称长度必须在1-50之间")
            String name)
    {
        return Result.success(cinemaService.getCinemasByLocation(name));
    }

    /**
     * 根据城市代码获取影院列表
     * @param cityCode 城市代码
     * @return 影院列表
     */
    @GetMapping("/list-by-city/{cityCode}")
    public Result<List<Cinema>> getCinemaByCityCode(
            @PathVariable("cityCode")
            @NotBlank(message = "城市代码不能为空")
            @Pattern(regexp = "^[0-9]{6}$", message = "城市代码必须是6位数字")
            String cityCode)
    {
        return Result.success(cinemaService.getCinemasByCityCode(cityCode));
    }

    /**
     * 获取影院详情
     * @param cinemaId 影院ID
     * @return 影院详情
     */
    @GetMapping("/detail")
    public Result<Cinema> getCinemaDetail(
            @RequestParam("cinemaId")
            @NotNull(message = "影院ID不能为空")
            @Positive(message = "影院ID必须为正数")
            Long cinemaId)
    {
        return Result.success(cinemaService.getCinemaDetail(cinemaId));
    }

}
