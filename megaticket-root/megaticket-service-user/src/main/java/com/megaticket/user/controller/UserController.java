package com.megaticket.user.controller;

import com.megaticket.common.result.Result;
import com.megaticket.user.dto.LoginRequest;
import com.megaticket.user.dto.LoginResponse;
import com.megaticket.user.dto.RegisterRequest;
import com.megaticket.user.entity.User;
import com.megaticket.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * @author Yang JunJie
 * @since 2026/1/19
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return Result.success(response);
    }
    
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.success();
    }
    
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestHeader("X-User-Id") Long userId) {
        User user = userService.getUserInfo(userId);
        return Result.success(user);
    }
}
