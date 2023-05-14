package com.k1.reggie.controller;

import com.k1.reggie.common.R;
import com.k1.reggie.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author：kkoneone11
 * @name：HttpSessionListenerController
 * @Date：2023/4/9 11:24
 */
@Slf4j
@RestController
@RequestMapping("/listener")
public class HttpSessionListenerController {

        @GetMapping("/total")
        public R<String> getTotalUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            Integer count = (Integer) request.getSession().getServletContext().getAttribute("count");

            String s = "当前在线人数："+count;
            return R.success(s);
        }

}
