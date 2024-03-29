package com.xujian.es.controller;

import com.alibaba.fastjson.JSON;
import com.xujian.es.domain.common.BaseResult;
import com.xujian.es.domain.common.Page;
import com.xujian.es.domain.model.BookModel;
import com.xujian.es.domain.vo.BookRequestVO;
import com.xujian.es.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by xujian on 2019-07-31
 */
@Slf4j
@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * 列表分页查询
     */
    @GetMapping("/list")
    public BaseResult list(BookRequestVO bookRequestVO) {
        Page<BookModel> page = bookService.list(bookRequestVO);
        if (null == page) {
            return BaseResult.error();
        }
        return BaseResult.ok(page);
    }

    /**
     * 查看文档
     */
    @GetMapping("/detail")
    public BaseResult detail(Integer id) {
        if (null == id) {
            return BaseResult.error("ID不能为空");
        }
        BookModel book = bookService.detail(id);
        return BaseResult.ok(book);
    }

    /**
     * 添加文档
     */
    @PostMapping("/add")
    public BaseResult add(@RequestBody BookModel bookModel) {
        bookService.save(bookModel);
        log.info("插入文档成功！请求参数: {}", JSON.toJSONString(bookModel));
        return BaseResult.ok();
    }

    /**
     * 修改文档
     */
    @PostMapping("/update")
    public BaseResult update(@RequestBody BookModel bookModel) {
        Integer id = bookModel.getId();
        if (null == id) {
            return BaseResult.error("ID不能为空");
        }
        BookModel book = bookService.detail(id);
        if (null == book) {
            return BaseResult.error("记录不存在");
        }
        bookService.update(bookModel);
        log.info("更新文档成功！请求参数: {}", JSON.toJSONString(bookModel));
        return BaseResult.ok();
    }

    /**
     * 删除文档
     */
    @GetMapping("/delete")
    public BaseResult delete(Integer id) {
        if (null == id) {
            return BaseResult.error("ID不能为空");
        }
        bookService.delete(id);
        return BaseResult.ok();
    }

    @GetMapping("/exist")
    public BaseResult exist(Integer id) {
        if (null == id) {
            return BaseResult.error("ID不能为空");
        }
        return BaseResult.ok(bookService.exist(id));
    }
}
