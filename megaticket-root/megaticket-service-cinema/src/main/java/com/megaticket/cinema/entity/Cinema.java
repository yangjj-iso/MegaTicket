package com.megaticket.cinema.entity;


import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.*;
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
    @NotBlank(message = "影院名称不能为空")
    @Size(min = 2, max = 50, message = "影院名称长度必须在2-50之间")
    private String name;

    // 所在城市的代码
    @NotBlank(message = "城市代码不能为空")
    @Pattern(regexp = "^[0-9]{6}$", message = "城市代码必须是6位数字")
    private String cityCode;

    // 影院地址
    @NotBlank(message = "影院地址不能为空")
    @Size(max = 200, message = "影院地址长度不能超过200")
    private String address;

    // 影院状态，0-关闭，1-营业
    @Min(value = 0, message = "影院状态只能为0或1")
    @Max(value = 1, message = "影院状态只能为0或1")
    private Integer status;

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
