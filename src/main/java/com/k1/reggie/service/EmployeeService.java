package com.k1.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.k1.reggie.common.R;
import com.k1.reggie.entity.Employee;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface EmployeeService extends IService<Employee> {

    /**
     * 自动登录获取参数方法
     * @param name
     * @param request
     * @param response
     * @return
     */
    public  R getUserMess(String name, HttpServletRequest request, HttpServletResponse response);
    public Employee getUserMess(String name);
}
