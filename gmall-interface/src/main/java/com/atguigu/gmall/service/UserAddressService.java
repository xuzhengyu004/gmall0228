package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.bean.UserInfo;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

import java.util.List;

public interface UserAddressService {

    //通过用户的ID查找用户的地址
    List<UserAddress> getUserAddressList(String id);


}
