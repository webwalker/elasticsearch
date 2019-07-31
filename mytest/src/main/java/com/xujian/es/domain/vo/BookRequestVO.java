package com.xujian.es.domain.vo;

import lombok.Data;

/**
 * Created by xujian on 2019-07-31
 */
@Data
public class BookRequestVO {
    private String name;
    private String author;
    private String status;
    private String sellTime;
    private String categories;

    private int pageNo;
    private int pageSize;
}
