package com.xujian.es.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xujian on 2019-08-01
 */
@RestController
public class SearchController {
    @RequestMapping
    public void search() {
        long begin = System.currentTimeMillis();

    }
}
