package com.boyu.snbe.config;

import com.boyu.snbe.common.SnException;
import com.boyu.snbe.common.servlet.SnResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理全局异常
     */
    @ExceptionHandler(Exception.class)
    public SnResponse handleException(Exception e) {
        if (e instanceof SnException) {
            SnResponse error = SnResponse.error();
            if (((SnException) e).getCode() != null) {
                error.put("code", ((SnException) e).getCode());
            }
            error.put("message", e.getMessage());
            error.put("success", false);
            return error;
        }
        return SnResponse.error();
    }
}
