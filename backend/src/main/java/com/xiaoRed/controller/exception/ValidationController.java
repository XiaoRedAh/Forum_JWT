package com.xiaoRed.controller.exception;

import com.xiaoRed.entity.RestBean;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 用于接口参数校验处理的控制器
 */
@Slf4j
@RestControllerAdvice
public class ValidationController {

    /**
     * 与SpringBoot保持一致，校验不通过打印警告信息，而不是直接抛出异常
     * @param exception 验证异常
     * @return 校验结果
     */
    @ExceptionHandler(ValidationException.class)
    public RestBean<Void> validateException(ValidationException exception){
        //仿照springmvc风格打印错误日志
        log.warn("Resolve: [{} : {}]", exception.getClass().getName(), exception.getMessage());
        //错误提示不要太详细，这样如果有人恶意来搞，也不容易知道内部具体细节
        return RestBean.failure(400, "请求参数有误");
    }
}
