package com.megaticket.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.megaticket.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 * @author Yang JunJie
 * @since 2026/1/19
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
