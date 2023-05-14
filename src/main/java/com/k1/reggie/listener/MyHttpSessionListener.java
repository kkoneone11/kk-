package com.k1.reggie.listener;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * @Author：kkoneone11
 * @name：MyHttpSessionListener
 * @Date：2023/4/9 11:06
 */
@Component
@Slf4j
public class MyHttpSessionListener implements HttpSessionListener {


    //记录管理员在线的数量
    public Integer count = 0;

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {

        count++;
        httpSessionEvent.getSession().getServletContext().setAttribute("count", count);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {

        count--;
        httpSessionEvent.getSession().getServletContext().setAttribute("count", count);
    }
}

