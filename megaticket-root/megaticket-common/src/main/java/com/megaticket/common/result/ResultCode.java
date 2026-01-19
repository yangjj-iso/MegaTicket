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
    UNAUTHORIZED(401, "未授权访问"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源未找到"),
    Too_Many_REQUESTS(429, "请求过多，稍后再试"),
    SERVER_OVERLOAD(503, "服务器过载"),
    // 业务错误码
    /*
    业务错误码按错误类别细分:
        1.10XX 用户类错误
        2.20XX 影院错误
        3.30XX 座位错误
        4.40XX 订单类错误
     */
    USER_NOT_LOGIN(1001, "用户未登录"),
    // 影院相关错误 (20XX)
    CINEMA_NOT_FOUND(2001, "影院不存在"),
    CINEMA_HALL_NOT_FOUND(2002, "影厅不存在"),
    CINEMA_NAME_INVALID(2003, "影院名称无效"),
    CINEMA_ADDRESS_INVALID(2004, "影院地址无效"),
    CINEMA_CITY_CODE_INVALID(2005, "城市代码无效"),
    CINEMA_STATUS_INVALID(2006, "影院状态无效"),
    CINEMA_HALL_ROWS_INVALID(2007, "影厅行数无效"),
    CINEMA_HALL_COLS_INVALID(2008, "影厅列数无效"),
    CINEMA_HALL_NAME_INVALID(2009, "影厅名称无效"),
    // 座位相关错误 (30XX)
    SCHEDULE_NOT_FOUND(3001, "场次不存在"),
    SEAT_NOT_FOUND(3002, "座位不存在"),
    SEAT_ALREADY_LOCKED(3003, "手慢了，座位已被锁定"),
    SEAT_SOLD_OUT(3004, "该座位已售出"),
    SEAT_LOCK_EXPIRED(3005, "座位锁定已过期"),
    SEAT_INVALID_ROW(3006, "座位行号无效"),
    SEAT_INVALID_COL(3007, "座位列号无效"),
    SEAT_RELEASE_FAILED(3008, "释放座位失败"),
    SEAT_STATUS_QUERY_FAILED(3009, "查询座位状态失败"),
    SEAT_SOLD_FAILED(3010, "标记座位已售出失败"),
    // 订单相关错误 (40XX)
    ORDER_CREATE_FAILED(4001, "创建订单失败"),
    ORDER_NOT_FOUND(4002, "订单不存在"),
    ORDER_STATUS_INVALID(4003, "订单状态无效"),
    ORDER_TIMEOUT(4004, "订单已超时"),
    ORDER_PAID(4005, "订单已支付");

    private final int code;
    private final String message;
}
