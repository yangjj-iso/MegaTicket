package com.megaticket.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.megaticket.pay.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付Mapper
 * @author Yang JunJie
 * @since 2026/1/19
 */
@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
}
