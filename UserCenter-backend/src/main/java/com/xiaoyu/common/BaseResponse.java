package com.xiaoyu.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xiaoyu
 * @version 1.0
 * @date 2023/3/12 21:52
 * 全局的接口返回处理类
 */

@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = 2071878518642821413L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 接口的数据
     */
    private T data;

    /**
     * 返回的消息
     */
    private String message;

    /**
     * 消息的描述
     */
    private String description;


    public BaseResponse(int  code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data, String message) {
       this(code, data , message, "");
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
       this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }


}
