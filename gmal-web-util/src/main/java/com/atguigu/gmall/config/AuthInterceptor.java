package com.atguigu.gmall.config;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.impl.Base64UrlCodec;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /*准备取得token的值放在cookie中 newToken是登录之后返回的值 */
        String token = request.getParameter("newToken");
        //把token保存到cookie CookieUtil 工具类
        if(token!=null){
            CookieUtil.setCookie(request,response,"token",token,WebConst.COOKIE_MAXAGE,false);
        }
        /*用户登之后，跳转其他的页面，newToken可能不存在，第一次登录的时候，已将newToken值放入cookie中*/
        if(token==null){
            token = CookieUtil.getCookieValue(request, "token", false);
        }
        /*当token不为空的时候，取出数据昵称，显示到页面上*/
        if(token!=null) {
            //创建一个方法取得token中的user对象数据，实际上就是解密
            Map map = getUserMapByToken(token);
            String nickName = (String) map.get("nickName");
            request.setAttribute("nickName", nickName);
            //完成页面
        }
        //  检查登录的代码，因为使用注解@LoginRequire 找到该注解下的方法，
        HandlerMethod handlerMethod = (HandlerMethod)handler;
        LoginRequire methodAnnotation = handlerMethod.getMethodAnnotation(LoginRequire.class);
        if (methodAnnotation!=null){
            // 找到需要认证当前用户登录状态，没有找到则跳转登录
            // 需要ip地址，token
            String remoteAddr = request.getHeader("x-forwarded-for");
            // 远程调用认证控制器
            // String token = request.getParameter("token");
            // String currentIp = request.getParameter("currentIp");
            String result = HttpClientUtil.doGet(WebConst.VERIFY_ADDRESS + "?token=" + token + "&currentIp=" + remoteAddr);
            if ("success".equals(result)){
                // 取出数据
                Map map = getUserMapByToken(token);
                String userId =(String) map.get("userId");
                request.setAttribute("userId",userId);
                return true;
            }else{
                // 登录
                if(methodAnnotation.autoRedirect()){
                    String  requestURL = request.getRequestURL().toString();
                    String encodeURL = URLEncoder.encode(requestURL, "UTF-8");
                    // 跳转到login页面
                    response.sendRedirect(WebConst.LOGIN_ADDRESS+"?originUrl="+encodeURL);
                    return false;
                }
            }
        }

        return true;
    }


    private  Map getUserMapByToken(String  token){
        /*分割token*/
        String tokenUserInfo = StringUtils.substringBetween(token, ".");
        /*解码*/
        Base64UrlCodec base64UrlCodec = new Base64UrlCodec();
        byte[] tokenBytes = base64UrlCodec.decode(tokenUserInfo);
        String tokenJson = null;
        try {
        /*将字节数组转换成字符串*/
            tokenJson = new String(tokenBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        /*将字符串转换成map集合*/
        Map map = JSON.parseObject(tokenJson, Map.class);
        return map;
    }

}