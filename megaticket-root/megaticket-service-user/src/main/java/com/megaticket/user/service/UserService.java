package com.megaticket.user.service;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.megaticket.common.exception.BusinessException;
import com.megaticket.common.result.ResultCode;
import com.megaticket.common.util.JwtUtil;
import com.megaticket.user.dto.LoginRequest;
import com.megaticket.user.dto.LoginResponse;
import com.megaticket.user.dto.RegisterRequest;
import com.megaticket.user.entity.User;
import com.megaticket.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 用户服务类
 * @author Yang JunJie
 * @since 2026/1/19
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    
    public LoginResponse login(LoginRequest request) {
        if (!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR);
        }
        
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("username", request.getUsername());
        User user = userMapper.selectOne(qw);
        
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        
        if (user.getStatus() != 1) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        
        String token = jwtUtil.generateToken(user.getId());
        
        log.info("用户登录成功, userId={}, username={}", user.getId(), user.getUsername());
        
        return new LoginResponse(user.getId(), user.getUsername(), user.getNickname(), token);
    }
    
    public void register(RegisterRequest request) {
        if (!StringUtils.hasText(request.getUsername()) || !StringUtils.hasText(request.getPassword())) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR);
        }
        
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("username", request.getUsername());
        Long count = userMapper.selectCount(qw);
        
        if (count > 0) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR);
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setNickname(StringUtils.hasText(request.getNickname()) ? request.getNickname() : request.getUsername());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        userMapper.insert(user);
        
        log.info("用户注册成功, username={}", user.getUsername());
    }
    
    public User getUserInfo(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ResultCode.PARAM_VALID_ERROR);
        }
        
        User user = userMapper.selectById(userId);
        
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        
        user.setPassword(null);
        
        return user;
    }
}
