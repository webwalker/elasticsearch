package com.xujian.es.domain.common;

import com.xujian.es.domain.model.BookModel;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by xujian on 2019-07-31
 */
@Data
public class BaseResult implements Serializable {
    private int code;
    private boolean success;
    private String msg;
    private Object data;

    public BaseResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
        if (code == 0) {
            success = true;
        }
    }

    public BaseResult(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        if (code == 0) {
            success = true;
        }
    }

    public static BaseResult ok() {
        return new BaseResult(0, "成功");
    }

    public static BaseResult ok(Object data) {
        return new BaseResult(0, "成功", data);
    }

    public static BaseResult ok(Page page) {
        return new BaseResult(0, "成功", page);
    }

    public static BaseResult ok(BookModel model) {
        return new BaseResult(0, "成功", model);
    }

    public static BaseResult error() {
        return new BaseResult(-1, "出错了.");
    }

    public static BaseResult error(String msg) {
        return new BaseResult(-1, msg);
    }
}
