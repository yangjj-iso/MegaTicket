package com.megaticket.cinema.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 影院厅实体类
 * author Yang JunJie
 * since 2026/1/14
 */
@Data
@TableName("cinema_hall")
public class CinemaHall {

    // 主键ID
    @TableId(type= IdType.AUTO)
    private Long id;

    // 影院厅id
    private Long cinemaId;

    // 影院厅名称
    private String name;

    // 座位Rows
    private int totalRows;

    // 座位Columns
    private int totalCols;

    // 影院厅标签
    private List<String> tags;

    // 逻辑删除标志，0-未删除，1-已删除
    @TableLogic
    private int isDeleted;

    // 创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
