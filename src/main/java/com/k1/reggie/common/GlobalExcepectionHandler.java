package com.k1.reggie.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 底层原理是基于AOP代理全局的controller然后拦截到对应cont的方法，然后拦截全局异常
 */
//annotations是注解，备注头顶上加了RestController和Controller的类的都会被扫描到
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody//返回json数据
@Slf4j
public class GlobalExcepectionHandler {


    //该方法专门拦截SQLIntegrityConstraintViolationException抛出的问题
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error(ex.getMessage());
        //含Duplicate entry错误的判断
        if (ex.getMessage().contains("Duplicate entry")) {
            //将字符串按照每个空格分开
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("位置错误");
    }


    /**
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex) {
        log.error(ex.getMessage());
        //含Duplicate entry错误的判断
        return R.error(ex.getMessage());
    }
}
