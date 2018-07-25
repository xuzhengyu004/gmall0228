package com.atguigu.gmall.service;



import com.atguigu.gmall.bean.UserInfo;

import java.util.List;

public interface UserInfoService {

    //业务层必须手动添加
    public List<UserInfo> findall();

    //like
    public List<UserInfo> findlike();


    //添加
    public void addUserInfo(UserInfo userInfo);

    //修改
    public void updateUserInfo(UserInfo userInfo);

    //删除
    public void deleteUserIndo(UserInfo id);

    public void updateUserInfo1(UserInfo userInfo);

    /*登录方法*/
    public UserInfo login(UserInfo userInfo);
    //验证方法 同过userId，查找userInfo 的信息
    UserInfo verify(String userId);
}
