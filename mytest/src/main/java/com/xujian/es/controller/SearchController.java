package com.xujian.es.controller;

import com.xujian.es.domain.common.BaseResult;
import com.xujian.es.domain.common.Page;
import com.xujian.es.domain.model.IndexResourceInfoModel;
import com.xujian.es.domain.vo.IndexRequestVO;
import com.xujian.es.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 请先初始化数据库，然后执行elasticsearch-data的CREATEINDEX服务创建ES索引
 * Created by xujian on 2019-08-01
 */
@RestController
@Slf4j
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private SearchService service;

    //http://localhost:8080/search/search_1?pageNo=2&pageSize=10
    @RequestMapping("/search_1")
    public BaseResult search(IndexRequestVO requestVO) {
        try {
            Page<IndexResourceInfoModel> page = service.search(requestVO);
            if (null == page) {
                return BaseResult.error();
            }
            return BaseResult.ok(page);
        } catch (Exception e) {
            log.error("出错了，错误信息如下：");
            e.printStackTrace();
        }
        return BaseResult.error();
    }
}
