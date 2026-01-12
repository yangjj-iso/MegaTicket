package com.megaticket.common.result;


import lombok.Data;

import java.io.Serializable;

/**
 * 结果类,附带结果的数据
 * author Yang JunJie
 * since 2026/1/12
 */
@Data
public class Result<T> implements Serializable {

    private Integer code;
    private String message;
    private T data;

    //构造方法
    private Result(Integer code, String msg, T data) {
        this.code = code;
        this.message = msg;
        this.data = data;
    }

    //成功返回带有数据
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(),ResultCode.SUCCESS.getMessage(),data);
    }

    //成功返回,无数据
    public static <T> Result<T> success() {
        return success(null);
    }

    //失败返回
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code,msg,null);
    }

    //失败返回,错误信息自定义
    public static <T> Result<T> error(Integer code, String msg, T data) {
        return new Result<>(code,msg,data);
    }

    //失败返回,结果代码枚举
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(),resultCode.getMessage(),null);
    }
}
