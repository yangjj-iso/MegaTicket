package com.megaticket.cinema.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.*;
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
    @NotNull(message = "影院ID不能为空")
    @Positive(message = "影院ID必须为正数")
    private Long cinemaId;

    // 影院厅名称
    @NotBlank(message = "影院厅名称不能为空")
    @Size(min = 1, max = 20, message = "影院厅名称长度必须在1-20之间")
    private String name;

    // 座位Rows
    @NotNull(message = "座位行数不能为空")
    @Min(value = 1, message = "座位行数至少为1")
    @Max(value = 50, message = "座位行数最多为50")
    private Integer totalRows;

    // 座位Columns
    @NotNull(message = "座位列数不能为空")
    @Min(value = 1, message = "座位列数至少为1")
    @Max(value = 100, message = "座位列数最多为100")
    private Integer totalCols;

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
