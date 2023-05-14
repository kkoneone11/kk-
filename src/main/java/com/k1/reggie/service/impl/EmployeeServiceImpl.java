package com.k1.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.k1.reggie.common.R;
import com.k1.reggie.entity.Employee;
import com.k1.reggie.mapper.EmployeeMapper;
import com.k1.reggie.service.EmployeeService;
import com.k1.reggie.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

@Service
@Slf4j
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    EmployeeService employeeService;

    //自动登录获取用户信息，并更新token和cookie
    @Override
    public R<Employee> getUserMess(String name, HttpServletRequest request, HttpServletResponse response) {

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<Employee>();
        queryWrapper.eq(Employee::getUsername, name);
        //因为Username是唯一的所以可以用getOne方法去查处一个唯一的数据返回给ee
        Employee emp = employeeService.getOne(queryWrapper);

        //jwt生成token,包含用户姓名
        String token = JwtUtils.getJwtToken(name);

        //更新token，token存入redis,7天有效期，具体自己设置
        redisTemplate.opsForValue().set(name, token, Duration.ofDays(7));

        ResponseCookie cookie = ResponseCookie.from("token", token)
                .path("/")            // path
                .maxAge(60 * 60 * 24 * 7)    // 有效期
                //以下这两项是设置https共享cookie
                .secure(true)
                .sameSite("None")
                .build();
        // 设置Cookie Header
        response.setHeader("Set-Cookie", cookie.toString());
        log.info(cookie.toString());//打印看看secure和sameSite是否设置成功

        return R.success(emp);
    }

    //重载一个方法
    public Employee getUserMess(String name){
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<Employee>();
        queryWrapper.eq(Employee::getUsername, name);
        //因为Username是唯一的所以可以用getOne方法去查处一个唯一的数据返回给ee
        Employee emp = employeeService.getOne(queryWrapper);
        return emp;
    }
}


