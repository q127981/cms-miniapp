package com.mrguan.tcyhwapp.handler;

import com.mrguan.tcyhwapp.entity.BasicResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Arrays;

/**
 * 统一处理所有返回值，对其进行封装
 *
 * @author MrGuan
 * @date 2019-8-9 11:02:44
 */
@RestControllerAdvice
@Slf4j
public class JsonResponseBodyAdvice implements ResponseBodyAdvice {

    private final String ERROR_URL = "/error";

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        //排除swagger相关接口
        String[] swaggerUrl = {"/v2/api-docs", "/swagger-resources", "/swagger-resources/configuration/ui", "/swagger-resources/configuration/security", "/error"};
        String path = serverHttpRequest.getURI().getPath();
        if (ERROR_URL.equals(path) && !(o instanceof BasicResult)) {
            log.info("接口访问404了...");
            return BasicResult.error(1, "服务器异常！");
        }
        if (Arrays.asList(swaggerUrl).contains(serverHttpRequest.getURI().getPath())) {
            return o;
        }
        Object result = null;
        //String类型需要做特殊处理
        if (o instanceof String) {
            result = JSONChange.objToJson(BasicResult.success(o));
//            log.info("接口：{}，返回值：{}", path, JSONObject.toJSONString(result));
            return result;
        }
        if (o instanceof BasicResult) {
            result = o;
        } else {
            result = BasicResult.success(o);
        }
//        log.info("接口：{}，返回值：{}", path, JSONObject.toJSONString(result));
        return result;
    }
}
