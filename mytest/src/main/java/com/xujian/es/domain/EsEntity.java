package com.xujian.es.domain;

import lombok.Data;

/**
 * Created by xujian on 2019-08-01
 */
@Data
public final class EsEntity<T> {
    private String id;
    private T data;

    public EsEntity() {
    }

    public EsEntity(String id, T data) {
        this.data = data;
        this.id = id;
    }
}