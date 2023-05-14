package com.k1.reggie.controller;


import com.k1.reggie.common.R;
import com.k1.reggie.entity.Orders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/orderDetail")
public class OrderDetailController {
    @Autowired
    OrderDetailController orderDetailController;


}
