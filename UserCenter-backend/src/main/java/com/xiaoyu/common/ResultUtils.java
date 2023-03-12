package com.xiaoyu.common;

/**
 * @author xiaoyu
 * @version 1.0
 * @date 2023/3/12 21:52
 * 全局接口返回的工具类
 */
public class ResultUtils {


    /**
     * 成功
     * @param data 返回的数据
     * @param <T> 返回的数据类型
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败
     * @param errorCode 错误状态码
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     * @param code
     * @param message
     * @param description
     * @return
     */
    public static BaseResponse error(int code, String message, String description) {
        return new BaseResponse<>(code, null, message, description);
    }

    /**
     * 失败
     * @param errorCode 错误状态码
     */
    public static BaseResponse error(ErrorCode errorCode, String message, String description) {
        return new BaseResponse<>(errorCode.getCode(), null,  message, description);
    }

    /**
     * 失败
     * @param errorCode 错误状态码
     */
    public static BaseResponse error(ErrorCode errorCode,  String description) {
        return new BaseResponse<>(errorCode.getCode(), null,  errorCode.getMessage(), description);
    }



}
