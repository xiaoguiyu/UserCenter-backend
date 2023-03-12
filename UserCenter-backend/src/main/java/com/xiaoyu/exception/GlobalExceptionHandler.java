package com.xiaoyu.exception;

import com.xiaoyu.common.BaseResponse;
import com.xiaoyu.common.ErrorCode;
import com.xiaoyu.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author xiaoyu
 * @version 1.0
 * @date 2023/3/13 0:18
 * 全局的异常处理器
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(CustomException.class)
    public BaseResponse customExceptionHandler(CustomException e) {

        log.error("CustomException: " + e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse customExceptionHandler(RuntimeException e) {

        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
    }


}
