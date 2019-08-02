package com.xujian.es.exception;

import org.springframework.util.StringUtils;

/**
 * Created by xujian on 2019-08-01
 */
public class CustomException extends RuntimeException {

    private String message;
    private int code;

    public CustomException() {
        this(null, null, null);
    }

    public CustomException(String message) {
        this(message, null, null);
    }

    public CustomException(Exception e) {
        this(null, null, e);
    }

    public CustomException(String message, Exception e) {
        this(message, null, e);
    }

    public CustomException(String message, Integer code, Exception e) {
        super(e);
        this.message = message == null ? "" : message;
        this.code = code == null ? 0 : code;
    }

    @Override
    public String getMessage() {
        if (StringUtils.isEmpty(this.message)) {
            return super.getMessage();
        } else {
            return this.message;
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}