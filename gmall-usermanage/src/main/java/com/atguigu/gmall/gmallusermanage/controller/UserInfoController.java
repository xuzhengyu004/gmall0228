package com.atguigu.gmall.gmallusermanage.controller;


import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @RequestMapping(value="findall")
    @ResponseBody
    public List<UserInfo> findall(){
        List<UserInfo> userInfoList = userInfoService.findall();
        //iter
        for (UserInfo userInfo : userInfoList) {
            System.out.println(userInfo);
        }
        return userInfoList;
    }

    @RequestMapping(value="findlike")
    @ResponseBody
    public List<UserInfo> findlike(){
        List<UserInfo> userInfoList = userInfoService.findlike();
        //iter
        for (UserInfo userInfo : userInfoList) {
            System.out.println(userInfo);
        }
        return userInfoList;
    }

    @RequestMapping(value="findadd")
    @ResponseBody
    public void findadd(UserInfo userInfo){
        userInfo.setName("大宝贝123");
        userInfo.setPasswd("456");
        userInfoService.addUserInfo(userInfo);
    }

    @RequestMapping(value="findupdate")
    @ResponseBody
    public void findupdate(UserInfo userInfo){
        userInfo.setId("1002");
        userInfo.setLoginName("孙悟空");
        userInfo.setName("齐天大圣");
        userInfoService.updateUserInfo(userInfo);
    }
    @RequestMapping(value="findupdate1")
    @ResponseBody
    public void findupdate1(UserInfo userInfo){
        userInfo.setLoginName("孙悟空");
        userInfo.setName("齐天大圣123");
        userInfoService.updateUserInfo1(userInfo);
    }

    @RequestMapping(value="finddelete")
    @ResponseBody
    public void finddelete(UserInfo userInfo){
        userInfo.setId("1003");
        userInfoService.deleteUserIndo(userInfo);
    }

}
