package com.mrguan.tcyhwapp.entity;

import lombok.Data;

@Data
public class BasicException extends RuntimeException {

    private Integer code;

    public BasicException(String msg) {
        super(msg);
        this.printStackTrace();

    }

    public BasicException(SysCode sysCode) {
        super(sysCode.MESSAGE);
        this.code = sysCode.CODE;
        this.printStackTrace();
    }

    public BasicException(Integer code, String msg) {

        super(msg);

        this.code = code;
        this.printStackTrace();
    }

}
 