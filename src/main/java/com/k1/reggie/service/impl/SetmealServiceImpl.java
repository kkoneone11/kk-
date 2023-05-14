package com.k1.reggie.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.k1.reggie.common.CustomException;
import com.k1.reggie.dto.SetmealDto;
import com.k1.reggie.entity.Setmeal;
import com.k1.reggie.entity.SetmealDish;
import com.k1.reggie.mapper.SetmealMapper;
import com.k1.reggie.service.SetmealDishService;
import com.k1.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     *
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //先保存菜品基本信息，操作setmeal表
        this.save(setmealDto);

        //保存套餐的dish到SetmealDish中
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //执行操作

        setmealDishService.saveBatch(setmealDishes);

    }

    /**
     * 删除套餐
     *
     * @param ids
     */
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态 确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);

        //调用ServiceImpl的count方法
        int count = this.count(queryWrapper);
        if (count > 0) {
            //如果不能删除，抛出业务异常
            throw new CustomException("套餐正在售卖中,不能删除");
        }


        //如果可以删除，先删除套餐表中的数据
        this.removeByIds(ids);

        //删除关系表中的数据,但ids对应的是setmeal套餐的id不是setmealDish联系表中对应套餐和菜品的id---setmeal_dish
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId);
        setmealDishService.remove(lambdaQueryWrapper);
    }
}
