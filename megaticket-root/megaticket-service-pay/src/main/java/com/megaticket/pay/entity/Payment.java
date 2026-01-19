package com.megaticket.pay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体类
 * @author Yang JunJie
 * @since 2026/1/19
 */
@Data
@TableName("tb_payment")
public class Payment {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long orderId;
    
    private Long userId;
    
    private BigDecimal amount;
    
    private String payMethod;
    
    private String transactionId;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private LocalDateTime payTime;
}
