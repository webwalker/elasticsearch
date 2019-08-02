package com.xujian.es.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by xujian on 2019-08-01
 */
@Slf4j
public class EsException extends CustomException {

    public EsException(Exception e) {
        super("当前服务不可用，请稍后重试", 0, e);
    }
}
