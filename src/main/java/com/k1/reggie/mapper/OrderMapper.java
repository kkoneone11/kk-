package com.k1.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.k1.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {

}