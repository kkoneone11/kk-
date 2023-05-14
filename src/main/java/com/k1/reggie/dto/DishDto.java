package com.k1.reggie.dto;

import com.k1.reggie.entity.Dish;
import com.k1.reggie.entity.DishFlavor;
import com.k1.reggie.entity.Dish;
import com.k1.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * 因为前端传回来的数据和实体类中属性不完全一致，而Dto接收用来拓展的该实体类，使其含有其他属性
 */
@Data
public class DishDto extends Dish {

    //菜品对应的口味数据
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
