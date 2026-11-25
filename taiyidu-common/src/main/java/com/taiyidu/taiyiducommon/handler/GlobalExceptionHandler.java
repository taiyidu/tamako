package com.taiyidu.taiyiducommon.handler;

import com.taiyidu.taiyiducommon.exception.BusinessException;
import com.taiyidu.taiyiducommon.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice // 核心注解：拦截所有 Controller 抛出的异常
public class GlobalExceptionHandler {
    /**
     * 1. 拦截自定义的业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

//    /**
//     * 2. 拦截参数校验异常（比如 @Valid 校验失败）
//     * 需要引入 spring-boot-starter-validation 依赖
//     */
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
//        String msg = e.getBindingResult().getFieldError() != null
//                ? e.getBindingResult().getFieldError().getDefaultMessage()
//                : "参数校验失败";
//        log.warn("参数校验异常: {}", msg);
//        return Result.error(400, msg);
//    }

    /**
     * 3. 拦截所有未预料的系统异常（兜底）
     * 对于系统异常，千万不要把详细的错误堆栈直接返回给前端（不安全），只返回笼统提示，把详细信息记录到日志。
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: ", e); 
        return Result.error(500, "系统繁忙，请稍后再试");
    }
}