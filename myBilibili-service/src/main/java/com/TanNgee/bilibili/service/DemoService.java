package com.TanNgee.bilibili.service;

import com.TanNgee.bilibili.dao.DemoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author TanNgee
 * @Date 2022/6/21 21:29
 **/
@Service
public class DemoService {
    @Autowired
    private DemoDao demoDao;

    public Long query(Long id) {
        return demoDao.query(id);
    }
}
