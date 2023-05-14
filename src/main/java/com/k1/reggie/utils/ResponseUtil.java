package com.k1.reggie.utils;

/**
 * @Author：kkoneone11
 * @name：ResponseUtil
 * @Date：2023/4/10 20:15
 */

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.k1.reggie.common.R;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
        * 用于处理响应（HttpServletResponse）的工具类
        */
//public class ResponseUtil {
//
//    public static void out(HttpServletRequest request, HttpServletResponse response, R r) {
//        ObjectMapper mapper = new ObjectMapper();
//        response.setStatus(HttpStatus.OK.value());
//        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
//        /**
//         * 响应跨域配置
//         */
//        // 响应标头指定 指定可以访问资源的URI路径
//        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
//        //响应标头指定响应访问所述资源到时允许的一种或多种方法
//        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
//        //设置 缓存可以生存的最大秒数
//        response.setHeader("Access-Control-Max-Age", "3600");
//        //设置  受支持请求标头
//        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
//        // 指示的请求的响应是否可以暴露于该页面。当true值返回时它可以被暴露
//        response.setHeader("Access-Control-Allow-Credentials", "true");
//        try {
//            mapper.writeValue(response.getWriter(), r);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class ResponseUtil {
    public static void write(HttpServletResponse response,Object o) throws IOException{
//        ;
            PrintWriter out = response.getWriter();
            out.write(JSON.toJSONString(R.success(o)));
            out.flush();
            out.close();
    }
}