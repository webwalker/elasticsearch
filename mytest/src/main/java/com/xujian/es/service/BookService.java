package com.xujian.es.service;

import com.xujian.es.domain.common.Page;
import com.xujian.es.domain.model.BookModel;
import com.xujian.es.domain.vo.BookRequestVO;

/**
 * Created by xujian on 2019-07-31
 */
public interface BookService {
    Page<BookModel> list(BookRequestVO bookRequestVO);

    void save(BookModel bookModel);

    void update(BookModel bookModel);

    void delete(int id);

    BookModel detail(int id);
}
