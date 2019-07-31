package com.xujian.es.domain.common;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xujian on 2019-07-31
 */
@AllArgsConstructor
@Data
public class Page<T> implements Serializable {
    private int pageNumber;
    private int pageSize;
    private long totalCount;

    private List<T> pageList;
}
