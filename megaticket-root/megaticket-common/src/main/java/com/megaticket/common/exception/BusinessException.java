package com.megaticket.common.exception;

import com.megaticket.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常类
 * author Yang JunJie
 * since 2026/1/12
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ResultCode resultCode;

    // 直接传枚举，规范抛出异常的方式
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }
}
