package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.CartInfo;

import java.util.List;

public interface CartService {

    /*添加购物车*/
    public  void  addToCart(String skuId,String userId,Integer skuNum);

    /*根据用户Id查询cookie中的购物车信息*/
    List<CartInfo> getCartList(String userId);

    /*将cookie中的信息和DB中的信息合并*/
    List<CartInfo> mergeToCartList(List<CartInfo> cartListFromCookie, String userId);
    /*操作DB，检查选中状态*/
    void checkCart(String userId, String skuId, String isChecked);
    /*获取购物车选中的列表*/
    List<CartInfo> getCartCheckedList(String userId);
}