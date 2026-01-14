package com.megaticket.cinema.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;


import java.time.LocalDateTime;

/**
 * 影院实体类
 * author Yang JunJie
 * since 2026/1/14
 */
@Data
@TableName("cinema")
public class Cinema {

    // 主键ID
    @TableId(type= IdType.AUTO)
    private Long id;

    // 影院名称
    private String name;

    // 所在城市的代码
    private String cityCode;

    // 影院地址
    private String address;

    // 影院状态，0-关闭，1-营业
    private int status;

    // 逻辑删除标志，0-未删除，1-已删除
    @TableLogic
    private  int isDeleted;

    // 创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
