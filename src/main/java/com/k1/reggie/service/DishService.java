package com.k1.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.k1.reggie.common.R;
import com.k1.reggie.dto.DishDto;
import com.k1.reggie.entity.Dish;
import org.springframework.beans.factory.annotation.Autowired;

public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和菜品口味
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品
    public void updateWithFlavor(DishDto dishDto);

}
