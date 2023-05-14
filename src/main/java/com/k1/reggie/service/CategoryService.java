package com.k1.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.k1.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    /**
     * 设置一个根据自己业务来删除的方法
     *
     * @param id
     */
    public void remove(Long id);
}
