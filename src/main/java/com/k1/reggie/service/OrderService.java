package com.k1.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.k1.reggie.common.R;
import com.k1.reggie.entity.Orders;
import com.k1.reggie.entity.Orders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface OrderService extends IService<Orders> {
    /**
     * 提交订单
     *
     * @return
     */
    public void submit(Orders orders);
}
