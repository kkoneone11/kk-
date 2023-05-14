package com.k1.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.k1.reggie.common.BaseContext;
import com.k1.reggie.common.R;
import com.k1.reggie.entity.Employee;
import com.k1.reggie.service.EmployeeService;
import com.k1.reggie.utils.JwtUtils;
import com.k1.reggie.utils.ResponseUtil;
import lombok.extern.slf4j.Slf4j;

import org.openjsse.sun.security.ssl.CookieExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")//urlPatterns= "/*"是指所有请求都进过滤器
@Slf4j
public class LoginCheckFilter implements Filter {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    EmployeeService employeeService;

    //路径通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //整型提升
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1、获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求:{}", requestURI);

        //不需要拦截的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                //此处不拦截backend和front下所有的页面是因为下面的页面可以被查看但是要做的就是不把数据渲染上去
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };


        //2、判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3、如果不需要处理，则直接放行
        if (check) {
            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }


        Cookie cookie = JwtUtils.getToken(request);
        //如果没有cookie证明用户还未登陆过，则往下走，要用账号密码登陆
        if(cookie==null){
            filterChain.doFilter(request,response);
        }else {
            boolean isValid = JwtUtils.checkTokenByCookie(cookie,redisTemplate);
            if(isValid){
//                response.sendRedirect("index.html");
                String empname = JwtUtils.getUsername(cookie.getValue());//从token中获得用户名
                //方法1
//                R r = employeeService.getUserMess(empname,request,response); //获取用户信息
                //方法2
                Employee emp = employeeService.getUserMess(empname);//通过empname获取用户信息

                log.info("员工id为{}", request.getSession().getAttribute("employee"));
                //方法1
//                ResponseUtil.write(response,r);//使用ResponseUtil工具类返回响应
                //方法2
//                response.getWriter().write(JSON.toJSONString(R.success(r)));//使用getWriter()但报错
                //方法3
//                response.getOutputStream().write(JSON.toJSONBytes(R.success(r))); //使用getOutputStream()返回，无报错但因为返回数据不对因而无法正确显示，要测试的话要先注释掉下面4-1的员工代码

                //方法4 成功后可以生成一个url发送给login
                // Create URL object
//                URL obj = new URL("/employee/login");
//
//                // Open connection
//                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//                // Set request method
//                con.setRequestMethod("POST");
//
//                // Set request headers
//                con.setRequestProperty("User-Agent", "Mozilla/5.0");
//                con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
//
//                // Enable output and set content type
//                con.setDoOutput(true);
//                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//
//                // Write data to output stream
//                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
//                wr.write(String.valueOf(emp));
//                wr.flush();
//                wr.close();


                filterChain.doFilter(request, response);
                return;
            }else{
                filterChain.doFilter(request,response);
            }
        }
        //4-1、判断员工登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee") != null) {
            log.info("员工已登陆，员工id为{}", request.getSession().getAttribute("employee"));
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request, response);
            return;
        }

        //4-2、判断用户登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("user") != null) {
            log.info("用户已登录，用户id为：{}", request.getSession().getAttribute("user"));

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request, response);
            return;
        }


        log.info("用户未登陆");
        //5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        //将NOTLOGIN以json数据流的方式返回给前端，因为前端的拦截器检测到NOTLOGIN就会进行拦截
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        System.out.println("未登录,返回前端");

        return;

    }

    /**
     * 判断本次请求是否需要拦截,与urls里匹配上的就放行
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }

}
