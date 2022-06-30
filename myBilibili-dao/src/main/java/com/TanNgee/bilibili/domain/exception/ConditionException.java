package com.TanNgee.bilibili.domain.exception;

/**
 * @Author TanNgee
 * @Date 2022/6/22 21:49
 * 根据条件抛异常
 **/
public class ConditionException extends RuntimeException {


    private static final long serialVersionUID = 1L;
    private String code;

    public ConditionException(String code, String name) {
        super(name);
        this.code = code;
    }

    public ConditionException(String name) {
        super(name);
        code = "500"; // 通用错误代码
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
