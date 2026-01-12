package com.megaticket.common.result;

import com.sun.net.httpserver.Authenticator;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 结果代码类, 用于定义各种操作的结果状态码
 * author Yang JunJie
 * since 2026/1/12
 */

@Getter
@AllArgsConstructor
public enum ResultCode {

    // 通用状态码
    SUCCESS(200, "操作成功"),
    SYSTEM_ERROR(500, "系统繁忙，请稍后再试"),
    PARAM_VALID_ERROR(400, "参数校验失败"),
    // 业务错误码
    /*
    业务错误码按错误类别细分:
        1.10XX 用户类错误
        2.20XX 影院错误
        3.30XX 订单类错误
     */
    USER_NOT_LOGIN(1001, "用户未登录"),
    SEAT_ALREADY_LOCKED(3001, "手慢了，座位已被锁定"),
    SEAT_SOLD_OUT(3002, "该座位已售出"),
    ORDER_CREATE_FAILED(3003, "创建订单失败");

    private final int code;
    private final String message;
}
