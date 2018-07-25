package com.atguigu.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.passport.util.JwtUtil;
import com.atguigu.gmall.service.UserInfoService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {

    @Value("${token.key}")
    private String signKey;

    @Reference
    private UserInfoService userInfoService;

    @RequestMapping("index")
    public String index(HttpServletRequest request){
        // 取得跳转过来的url并保存
        String originUrl = request.getParameter("originUrl");
        request.setAttribute("originUrl",originUrl);
        return "index";
    }
    /*登录页码，登录成功后生成token*/
    @RequestMapping(value = "login",method = RequestMethod.POST)
    @ResponseBody
    public  String login(HttpServletRequest request,UserInfo userInfo){
        // 自动获取 通过nginx 的方向代理得到！location /{ proxy_pass  http://item.gmal.com }
        String ip = request.getHeader("X-forwarded-for");
        // 当用户登录成功之后，生成token。
        if (userInfo!=null){
            UserInfo info = userInfoService.login(userInfo);
            if (info!=null){
                // 生成token
                Map map = new HashMap<>();
                map.put("userId",info.getId());
                map.put("nickName",info.getNickName());
                String toekn = JwtUtil.encode(signKey, map, ip);
                System.out.println("token="+toekn);
                return  toekn;
            }
        }
        return  "fail";
    }


    // 跳转到其他的页面时候需要验证token
    @RequestMapping("verify")
    @ResponseBody
    public String verify(HttpServletRequest request){
        // 取得token，ip地址,key
        String token = request.getParameter("token");
        String currentIp = request.getParameter("currentIp");

        // 准备解密
        Map<String, Object> map = JwtUtil.decode(token, signKey, currentIp);

        if (map!=null){
            // 取得用户userId
            String userId = (String) map.get("userId");
            // 根据userId 判断redis中是否有登录用户,查找用户
            UserInfo userInfo = userInfoService.verify(userId);
            if (userInfo!=null){
                return "success";
            }
        }
        return "fail";
    }

}
