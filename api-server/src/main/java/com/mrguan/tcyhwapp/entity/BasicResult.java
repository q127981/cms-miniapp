package com.mrguan.tcyhwapp.entity;

import lombok.Data;


/**
 * @Auther: MrGuan
 * @Date: 2019/8/8 17:25
 * @Description:
 */
@Data
public class BasicResult<T> {

    /**
     * 成功代码
     */
    public static final int CODE_SUCCESS = 0;
    /**
     * 失败代码(大于0，默认为1)
     */
    public static final int CODE_FAILURE = 1;
    private Integer code;

    private String msg = "SUCCESS!";


    private T data;

    public BasicResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public BasicResult() {
    }

    public BasicResult(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public static <T> BasicResult success(T data) {
        return new BasicResult(CODE_SUCCESS, data);
    }

    public static <T> BasicResult success() {
        return success("SUCCESS!");
    }

    public static <T> BasicResult error(Integer code, String msg) {
        return new BasicResult(code, msg, null);
    }

    public static BasicResult error() {
        return new BasicResult(CODE_FAILURE, "ERROR!", null);
    }
}
