package com.mrguan.tcyhwapp.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

/**
 * @author MrGuan
 * 操作json的封装方法
 */
public class JSONChange {
    /*
     * 001.json转换成对象
     * @param:传入对象，json字符串
     * @return:Object
     */
    @SneakyThrows
    public static Object jsonToObj(Object obj, String jsonStr) {
        ObjectMapper mapper = new ObjectMapper();
        return obj = mapper.readValue(jsonStr, obj.getClass());
    }
    /*
     * 002.对象转换成json
     * @param:传入对象
     * @return:json字符串
     */

    @SneakyThrows
    public static String objToJson(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }
}