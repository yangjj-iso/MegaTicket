package com.megaticket.user.dto;

import lombok.Data;

/**
 * 注册请求DTO
 * @author Yang JunJie
 * @since 2026/1/19
 */
@Data
public class RegisterRequest {
    
    private String username;
    
    private String password;
    
    private String phone;
    
    private String email;
    
    private String nickname;
}
