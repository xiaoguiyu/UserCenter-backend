package com.xiaoyu.exception;

import com.xiaoyu.common.ErrorCode;


/**
 * @author xiaoyu
 * @version 1.0
 * @date 2023/3/12 22:43
 * 全局的自定义异常
 */

public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 782025847105837315L;


    private final int code;

    private final String description;

    public CustomException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public CustomException( ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public CustomException( ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }


    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
