package com.k1.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.k1.reggie.entity.User;
import com.k1.reggie.mapper.UserMapper;
import com.k1.reggie.service.UserService;
import com.k1.reggie.entity.User;
import com.k1.reggie.mapper.UserMapper;
import com.k1.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
