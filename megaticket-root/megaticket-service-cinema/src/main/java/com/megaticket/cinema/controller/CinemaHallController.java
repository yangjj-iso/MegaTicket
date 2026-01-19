package com.megaticket.cinema.controller;

import com.megaticket.cinema.entity.CinemaHall;
import com.megaticket.cinema.service.CinemaHallService;
import com.megaticket.common.result.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 影院厅控制器
 * author Yang JunJie
 * since 2026/1/14
 * version 1.0
 */
@RestController
@RequestMapping("/api/v1/cinema-hall")
@Slf4j
@AllArgsConstructor
@Validated
public class CinemaHallController {

    // 影院厅服务(构造器注入)
    private final CinemaHallService cinemaHallService;

    /**
     * 根据影院ID获取影厅列表
     * @param cinemaId 影院ID
     * @return 影厅列表
     */
    @GetMapping("/list/{cinemaId}")
    public Result<List<CinemaHall>> getHallsByCinemaId(
            @PathVariable("cinemaId")
            @NotNull(message = "影院ID不能为空")
            @Positive(message = "影院ID必须为正数")
            Long cinemaId) {
        return Result.success(cinemaHallService.getHallsByCinemaId(cinemaId));
    }

    /**
     * 根据影院ID和影厅名称模糊查询影厅列表
     * @param cinemaId 影院ID
     * @param hallName 影厅名称
     * @return 影厅列表
     */
    @GetMapping("/search")
    public Result<List<CinemaHall>> getHallsByCinemaIdAndName(
            @RequestParam("cinemaId")
            @NotNull(message = "影院ID不能为空")
            @Positive(message = "影院ID必须为正数")
            Long cinemaId,
            @RequestParam("hallName")
            @NotNull(message = "影厅名称不能为空")
            String hallName) {
        return Result.success(cinemaHallService.getHallsByCinemaIdAndName(cinemaId, hallName));
    }

    /**
     * 获取影厅详情
     * @param hallId 影厅ID
     * @return 影厅详情
     */
    @GetMapping("/detail/{hallId}")
    public Result<CinemaHall> getHallDetail(
            @PathVariable("hallId")
            @NotNull(message = "影厅ID不能为空")
            @Positive(message = "影厅ID必须为正数")
            Long hallId) {
        return Result.success(cinemaHallService.getHallDetail(hallId));
    }

    /**
     * 创建影厅
     * @param cinemaHall 影厅信息
     * @return 创建的影厅ID
     */
    @PostMapping("/create")
    public Result<Long> createHall(@Valid @RequestBody CinemaHall cinemaHall) {
        return Result.success(cinemaHallService.createHall(cinemaHall));
    }

    /**
     * 更新影厅信息
     * @param cinemaHall 影厅信息
     * @return 是否更新成功
     */
    @PutMapping("/update")
    public Result<Boolean> updateHall(@Valid @RequestBody CinemaHall cinemaHall) {
        return Result.success(cinemaHallService.updateHall(cinemaHall));
    }

    /**
     * 删除影厅（逻辑删除）
     * @param hallId 影厅ID
     * @return 是否删除成功
     */
    @DeleteMapping("/delete/{hallId}")
    public Result<Boolean> deleteHall(
            @PathVariable("hallId")
            @NotNull(message = "影厅ID不能为空")
            @Positive(message = "影厅ID必须为正数")
            Long hallId) {
        return Result.success(cinemaHallService.deleteHall(hallId));
    }
}
