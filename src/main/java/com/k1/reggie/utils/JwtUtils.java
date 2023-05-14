package com.k1.reggie.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;


/**
 * jwt 工具类
 */
public class JwtUtils {

    public static final long EXPIRE = 1000 * 60 * 60 * 24;
    public static final String APP_SECRET = "ukc8BDbRigUDaY6pZFfWus2jZWLPHO";

    //包含用户id和用户昵称
    public static String getJwtToken(String id, String nickname){

        String JwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setSubject("fish-user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .claim("id", id)
                .claim("nickname", nickname)
                .signWith(SignatureAlgorithm.HS256, APP_SECRET)
                .compact();

        return JwtToken;
    }

    //只包含 用户名
    public static String getJwtToken(String username){

        String JwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setSubject("fish-user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .claim("username", username)
                .signWith(SignatureAlgorithm.HS256, APP_SECRET)
                .compact();

        return JwtToken;
    }

    /**
     * 从header中取token
     * 根据token，获取其中的用户名，根据这个用户名查询redis这个token 是否有效
     */
    public static boolean checkTokenByRequest(HttpServletRequest request, RedisTemplate redisTemplate){
        String token = request.getHeader("token");
        System.out.println("请求头中的token为："+token);

        //如果token为空
        if(token==null || token.isEmpty()) return false;

        //根据token获取用户名
        String username = JwtUtils.getUsername(token);
        System.out.println("token中获取的用户名为："+username);

        //判断token是否存储在redis中
        Object redisValue = redisTemplate.opsForValue().get(username);
        if(redisValue == null){
            return false;
        }
        return true;
    }

    /**
     * 从cookies中取token
     * 根据token，获取其中的用户名，根据这个用户名查询redis这个token 是否有效
     */
    public static boolean checkTokenByCookie(HttpServletRequest request, RedisTemplate redisTemplate){
        Cookie[] cookies = request.getCookies();
        String token = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals( "token")).findFirst().get().getValue();

        String username = JwtUtils.getUsername(token);
        System.out.println("token中获取的用户名为："+username);

        //判断token是否存储在redis中
        Object redisValue = redisTemplate.opsForValue().get(username);
        if(redisValue == null){
            return false;
        }
        return true;

    }

    public static boolean checkTokenByCookie(Cookie cookie, RedisTemplate redisTemplate){

        try {
            String username = JwtUtils.getUsername(cookie.getValue());
            System.out.println("token中获取的用户名为："+username);

            //判断token是否存储在redis中
            Object redisValue = redisTemplate.opsForValue().get(username);
            if(redisValue == null){
                return false;
            }
        }catch (Exception e){
            return false;
        }

        return true;

    }


    /**
     * 判断token是否存在与有效
     * @param jwtToken
     * @return
     */
    public static boolean checkToken(String jwtToken) {
        if(StringUtils.isEmpty(jwtToken)) return false;
        try {
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }



    /**
     * 判断token是否存在与有效
     * @param request
     * @return
     */
    public static boolean checkToken(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader("token");
            if(StringUtils.isEmpty(jwtToken)) return false;
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据token获取会员id
     * @param request
     * @return
     */
    public static String getMemberIdByJwtToken(HttpServletRequest request) {
        String jwtToken = request.getHeader("token");
        if(StringUtils.isEmpty(jwtToken))
            return "";
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        return (String)claims.get("id");
    }

    /**
     * 根据token获取用户名
     */
    public static String getUsername(String token){
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String)claims.get("username");
    }


    /**
     * 根据cookie获取用户名
     * @param request
     */
    public static String getUsernameByCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie:cookies) {
            if(cookie.getName().equals("token")){
                return JwtUtils.getUsername(cookie.getValue());
            }
        }
        return null;
    }

    public static boolean checkTokenByUsername(String username,String token,RedisTemplate redisTemplate) {
        //判断token是否存储在redis中
        Object redisValue = redisTemplate.opsForValue().get(username);
        return redisValue.equals(token);

    }

    //从token中获取用户名，并根据用户名和token去redis中判断该token是否有效
    public static boolean checkTokenByToken(String token,RedisTemplate redisTemplate) {
        String username = getUsername(token);
        if(StringUtils.isEmpty(username)){
            return false;
        }else {
            return checkTokenByUsername(username,token,redisTemplate);
        }

    }

    public static Cookie getToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            return null;
        }else{
            for (Cookie cookie:cookies) {
                if(cookie.getName().equals("token")){
                    return cookie;
                }
            }
        }
        return null;
    }
}
