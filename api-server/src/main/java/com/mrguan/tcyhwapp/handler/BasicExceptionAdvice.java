package com.mrguan.tcyhwapp.handler;

import com.mrguan.tcyhwapp.entity.BasicException;
import com.mrguan.tcyhwapp.entity.BasicResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Set;


@RestControllerAdvice
@Slf4j
public class BasicExceptionAdvice {


    @ExceptionHandler(value = Exception.class)
    public BasicResult defaultException(HttpServletRequest request, Exception e) {
        e.printStackTrace();
        log.debug("程序执行异常：{}", e.getMessage());
        return BasicResult.error();

    }


    @ExceptionHandler(value = BasicException.class)
    public BasicResult myException(HttpServletRequest request, BasicException e) {
//        e.printStackTrace();
        log.error("程序执行异常：{}", e.getMessage());
        Integer code = e.getCode();
        String message = e.getMessage();
        if (e.getCode() == null) {
            code = 1;
        }
        if (e.getMessage() == null) {
            message = "程序执行异常!";
        }
        return BasicResult.error(code, message);

    }

    /**
     * 400 - Bad Request
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public BasicResult handleHttpMessageNotReadableException(HttpServletRequest req, HttpMessageNotReadableException e) {
        log.error("parameter_validation_fail", e);
        return BasicResult.error(BasicResult.CODE_FAILURE, e.getMessage());
    }

    /**
     * 400 - Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BasicResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        FieldError error = result.getFieldError();
        String field = error.getField();
        String code = error.getDefaultMessage();
        String message = String.format("%s:%s", field, code);
        return BasicResult.error(BasicResult.CODE_FAILURE, message);
    }

//    /**
//     * 400 - Bad Request
//     */
//
//    @ExceptionHandler(BindException.class)
//    public BasicResult<Object> handleBindException(BindException e) {
//        log.error("parameter_bind_fail", e);
//        BindingResult result = e.getBindingResult();
//        FieldError error = result.getFieldError();
//        String field = error.getField();
//        String code = error.getDefaultMessage();
//        String message = String.format("%s:%s", field, code);
//
//        return BasicResult.error(BasicResult.CODE_FAILURE, message);
//
//    }

    /**
     * 400 - Bad Request
     */

    @ExceptionHandler(ConstraintViolationException.class)
    public BasicResult handleServiceException(ConstraintViolationException e) {
        log.error("parameter_validation_fail", e);
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        ConstraintViolation<?> violation = violations.iterator().next();
        String message = violation.getMessage();
        return BasicResult.error(BasicResult.CODE_FAILURE, message);
    }

    /**
     * 400 - Bad Request
     */

    @ExceptionHandler(ValidationException.class)
    public BasicResult handleValidationException(ValidationException e) {
        log.error("parameter_validation_fail", e);
        return BasicResult.error(BasicResult.CODE_FAILURE, e.getMessage());

    }

    /**
     * 400 - Bad Request
     */

    @ExceptionHandler(IllegalArgumentException.class)
    public BasicResult handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("parameter_validation_fail", e);
        return BasicResult.error(BasicResult.CODE_FAILURE, e.getMessage());

    }

    /**
     * 400 - Bad Request
     */

    @ExceptionHandler(IllegalStateException.class)
    public BasicResult handleIllegalStateException(IllegalStateException e) {
        log.error("parameter_validation_fail", e);
        return BasicResult.error(BasicResult.CODE_FAILURE, e.getMessage());

    }

    /**
     * 405 - Method Not Allowed
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public BasicResult handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("不支持当前请求方法", e);
        return BasicResult.error(BasicResult.CODE_FAILURE, e.getMessage());

    }

    /**
     * 415 - Unsupported Media Type
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public BasicResult handleHttpMediaTypeNotSupportedException(Exception e) {
        log.error("mediatype_not_support", e);
        return BasicResult.error(BasicResult.CODE_FAILURE, e.getMessage());

    }


    @ExceptionHandler({RuntimeException.class})
    public BasicResult runtimeException(RuntimeException e) {
        int errorCode = 1;
        log.error("SystemException，errorCode=" + errorCode, e);
        return BasicResult.error();
    }
}