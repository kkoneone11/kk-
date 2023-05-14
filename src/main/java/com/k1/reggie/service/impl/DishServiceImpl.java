package com.k1.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.k1.reggie.common.R;
import com.k1.reggie.dto.DishDto;
import com.k1.reggie.entity.Dish;
import com.k1.reggie.entity.DishFlavor;
import com.k1.reggie.mapper.DishMapper;
import com.k1.reggie.service.DishFlavorService;
import com.k1.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {


    @Autowired
    private DishFlavorService dishFlavorService;


    /**
     * 新增菜品，将数据保存到dish和dishflavor两个表中
     *
     * @param dishDto
     */
    @Transactional//涉及到多张表所以要开启事务
    public void saveWithFlavor(DishDto dishDto) {
        //保存到dish表中，此处用super和this都一样，因为都有save方法
        this.save(dishDto);

        //保存菜品口味到dish_flavor表，所以要用到dishFlavorService来操作对应的数据库
        //这里dishDto的Flavors中从返回数据中可以知道并没有dishId和id，所以要对dishDto再赋值后才能保存在表中，而dishId是在被存入dish表中才有的id
        Long dishId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();
        //通过流的方式获取一个个flavors然后赋值empid,最后再collect收集起来变回list数组
        flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 查询菜品，回显数据
     *
     * @param id
     * @return
     */

    public DishDto getByIdWithFlavor(Long id) {
        //根据id在dish表查询
        Dish dish = super.getById(id);

        //根据id查询菜品口味
        DishDto dishDto = new DishDto();
        //将dish中的基本信息拷贝进dishdto
        BeanUtils.copyProperties(dish, dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id != null, DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        //将菜品口味进行赋值
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表
        super.updateById(dishDto);

        //先删除对应id的flavor表里已经有的数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //更新新的flavor --dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        //如果直接进行保存flavor是没有对应的dishId对应
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }


}
