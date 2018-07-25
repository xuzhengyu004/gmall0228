package com.atguigu.gmall.gmallusermanage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.gmallusermanage.mapper.UserAddressMapper;
import com.atguigu.gmall.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;
@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    public List<UserAddress> getUserAddressList(String id) {
        //创建用户地址对象
        UserAddress userAddress = new UserAddress();
        //将用户id传递给对象
        userAddress.setId(id);
        //使用通用Mapper查出对象信息
        List<UserAddress> userAddresses = userAddressMapper.select(userAddress);
        //将信息返回
        return userAddresses;
    }
}
