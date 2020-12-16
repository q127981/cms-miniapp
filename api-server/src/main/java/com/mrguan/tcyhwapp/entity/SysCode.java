package com.mrguan.tcyhwapp.entity;

/**
 * 系统通用代码
 * 根据需要各系统继承此类以扩展消息描述
 *
 * @author MrGuan
 * @date 2019/08/08
 */
public class SysCode {

    public static final SysCode SUCCESS = new SysCode(0, "SUCCESS");
    public static final SysCode NOT_EXISTS = new SysCode(-1, "数据不存在");
    public static final SysCode TOKEN_NOT_NULL = new SysCode(1000, "请先登录");
    public static final SysCode CODE_ERROR = new SysCode(1001, "验证码已过期或不正确");
    public static final SysCode USER_ALREADY_EXISTS = new SysCode(1002, "用户已存在");
    public static final SysCode USER_NOT_EXISTS = new SysCode(1003, "用户不存在");
    public static final SysCode PASSWORD_ERROR = new SysCode(1004, "用户密码错误");
    public static final SysCode GET_WX_INFO_ERROR = new SysCode(1005, "获取微信用户信息失败");
    public static final SysCode SENSITIVE_ERROR = new SysCode(1006, "提交内容含有敏感信息，请检查后重新提交！");


    public Integer CODE;
    public String MESSAGE;

    private SysCode(Integer CODE, String MESSAGE) {
        this.CODE = CODE;
        this.MESSAGE = MESSAGE;
    }
}
