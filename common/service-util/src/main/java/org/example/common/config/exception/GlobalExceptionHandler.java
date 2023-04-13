package org.example.common.config.exception;

import org.example.common.result.Result;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理
 *
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail(e.getMessage()).message("出现异常");
    }
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public Result AccessDeniedError(AccessDeniedException e){
        return Result.fail(e.getMessage()).code(209).message("当前用户无访问权限");
    }
}
