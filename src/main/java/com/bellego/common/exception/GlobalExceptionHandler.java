package com.bellego.common.exception;

import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.common.result.ResultEnum;
import jakarta.validation.ConstraintViolationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException ex) {
        return ResultBuilder.error(ResultEnum.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, ConstraintViolationException.class, IllegalArgumentException.class})
    public Result<Void> handleValidationException(Exception ex) {
        String message = ex.getMessage();
        if (ex instanceof MethodArgumentNotValidException validException && validException.getBindingResult().getFieldError() != null) {
            message = validException.getBindingResult().getFieldError().getDefaultMessage();
        }
        if (ex instanceof BindException bindException && bindException.getBindingResult().getFieldError() != null) {
            message = bindException.getBindingResult().getFieldError().getDefaultMessage();
        }
        return ResultBuilder.error(ResultEnum.BAD_REQUEST, message);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception ex) {
        return ResultBuilder.error(ResultEnum.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}

