package com.TanNgee.bilibili.domain;

import java.util.List;

/**
 * 分页查询
 * @Author TanNgee
 * @Date 2022/6/25 11:12
 **/
public class PageResult<T> {

    private Integer total;

    private List<T> list;

    public PageResult(Integer total, List<T> list){
        this.total = total;
        this.list = list;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
