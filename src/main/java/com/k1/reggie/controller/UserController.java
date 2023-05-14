package com.k1.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.k1.reggie.common.R;
import com.k1.reggie.entity.User;
import com.k1.reggie.service.UserService;
import com.k1.reggie.utils.SMSUtils;
import com.k1.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 用户登录
     *
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        //此处也可以用StringUtils.isNotEmpty(phone)
        if (phone != null) {
            //随机生成4位验证码 并转换成字符串形式方便后期存储
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code:{}", code);

            //调用阿里云提供的短信服务API向手机发送短信
            //SMSUtils.sendMessage("k1饱了吗","",phone,code);

            //将生成的验证码和手机号保存到session，键值对
            //session.setAttribute(phone,code);

            //将生成的验证码缓存到redis中，并设置有效期为5分钟
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);


            return R.success("短信发送成功");
        }


        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    public R<String> login(@RequestBody Map map, HttpSession session) {
        //接收体中不能用User因为没有code属性，使用dto或者map键值对都可以
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从session中获得保存的验证码
        //Object codeSession = session.getAttribute(phone);
        //从redis中获取保存的验证码
        Object codeSession = redisTemplate.opsForValue().get(phone);
        //进行验证码比对
        if (codeSession != null && codeSession.equals(code)) {
            //比对成功，说明登陆成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            //判断当前手机号对应用户是否为新用户，如果是就自动完成注册
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            //登陆成功后删除redis中的验证码
            redisTemplate.delete(phone);
            return R.success("登录成功");

        }
        return R.error("登录失败");
    }





}
