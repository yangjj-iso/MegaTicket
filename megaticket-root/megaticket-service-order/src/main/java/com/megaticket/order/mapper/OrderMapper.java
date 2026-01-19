package com.megaticket.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.megaticket.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单Mapper
 * @author Yang JunJie
 * @since 2026/1/19
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
