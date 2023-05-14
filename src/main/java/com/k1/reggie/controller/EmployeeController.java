package com.k1.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.k1.reggie.common.BaseContext;
import com.k1.reggie.common.R;
import com.k1.reggie.entity.Employee;
import com.k1.reggie.service.EmployeeService;
import com.k1.reggie.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;


@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 用户登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    //HttpServletRequest是为了把对应的employee的id存储在session里方便调用,@RequestBody中是网页返回来的数据
    public R<Employee> login(HttpServletRequest request, HttpServletResponse response, @RequestBody Employee employee) {
        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        //将password转换成bytes这样就会进行md5转换
        password = DigestUtils.md5DigestAsHex(password.getBytes());


        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<Employee>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        //因为Username是唯一的所以可以用getOne方法去查处一个唯一的数据返回给ee
        Employee emp = employeeService.getOne(queryWrapper);


        //3、如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("登陆失败");
        }


        //4、密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("登陆失败");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账户已禁用");
        }

        //6、登录成功，返回登录成功结果
        //jwt生成token
        String token = JwtUtils.getJwtToken(emp.getUsername());
        //token存入redis，7天有效期
        redisTemplate.opsForValue().set(emp.getUsername(), token, Duration.ofDays(7));
        //token存入cookie返回给浏览器
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

        //将员工id存入Session
        request.getSession().setAttribute("employee", emp.getId());
        request.getSession().setAttribute("employeeName", emp.getUsername());

        request.getSession().setAttribute("phone",emp.getPhone());
        request.getSession().setAttribute("status",emp.getStatus());


        log.info("员工登陆为：{}", request.getSession().getAttribute("employee"));
        log.info("用户登陆为：{}", request.getSession().getAttribute("user"));

        return R.success(emp);
    }

    /**
     * 员工退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清除session中保存的员工id
        request.getSession().removeAttribute("employee");
        redisTemplate.delete("");
        System.out.println(request.getSession().getAttribute("employee"));
        System.out.println(request.getSession().getAttribute("user"));
        request.removeAttribute("user");
        return R.success("退出成功");
    }


    /**
     * 新增员工
     *
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工,员工信息:{}", employee.toString());
        //设置初始密码为123456，要进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        //获得当前登陆用户的id(创建这个员工的人)：存在了session中
//        Long empid=(Long)request.getSession().getAttribute("employee");
//        System.out.println("empid:{}"+empid);

//        employee.setCreateUser(empid);
//        employee.setUpdateUser(empid);


        employeeService.save(employee);

        return R.success("增加成功");
    }


    /**
     * 员工信息查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    //因为前端封装的数据不是Employee，用的是Page里的封装方法
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page:{},pagesize:{},name:{}", page, pageSize, name);

        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        //构造条件构造器，根据姓名查询，相当于给查询的语句附上的条件然后交给Service查询
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件 第一个是判断条件，第二是查询的对应字段名 ，第三个是传进去的值
        queryWrapper.like(StringUtils.hasText(name), Employee::getName, name);

        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);


        //执行查询，用page自带的查询方法,queryWrapper查询到结果后会把结果赋给pageInfo
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     *
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info(employee.toString());
        long id = Thread.currentThread().getId();
        log.info("线程id为{}", id);
//        Long empId=(Long)request.getSession().getAttribute("employee");
//        //查看不同
//        System.out.println(request.getSession().getId());
//        System.out.println(request.getSession().getAttribute("employee"));

//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);


        return R.success("员工修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("没有查询到该员工");
    }


}
