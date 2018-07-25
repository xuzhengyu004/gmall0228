package com.atguigu.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.config.CookieUtil;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Component
public class CartCookieHandler {
    // 定义购物车名称
    private String cookieCartName = "CART";
    // 设置cookie 过期时间
    private int COOKIE_CART_MAXAGE=7*24*3600;

    @Reference
    private ManageService manageService;


    public void  addToCart(HttpServletRequest request, HttpServletResponse response, String skuId, String userId, Integer skuNum){
        // 判断cookie中是否存在，先获取数据
        String cartJson  = CookieUtil.getCookieValue(request, cookieCartName, true);
        List<CartInfo> cartInfoList = new ArrayList<>();
        // 设置一个boolean 类型的变量
        boolean ifExist=false;
        if (cartJson!=null){
            // cartJson转换成对象 ,坑！购物车中应该会有很多条数据，所以该处应该是集合
            cartInfoList = JSON.parseArray(cartJson, CartInfo.class);
            for (CartInfo cartInfo : cartInfoList) {
                // 判断cookie中的数据跟添加的数据是否一致
                if (cartInfo.getSkuId().equals(skuId)){
                    // 对数量进行更新
                    cartInfo.setSkuNum(cartInfo.getSkuNum()+skuNum);
                    // 对价格的处理
                    cartInfo.setSkuPrice(cartInfo.getCartPrice());
                    ifExist=true;
                }
            }
        }
        // 没有该商品则添加
        if (!ifExist){
            // 根据当前的skuId查找数据，放到cartInfo 中
            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
            CartInfo cartInfo=new CartInfo();

            cartInfo.setSkuId(skuId);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuPrice(skuInfo.getPrice());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());

            cartInfo.setUserId(userId);
            cartInfo.setSkuNum(skuNum);
            // 将每个商品都放入集合中。
            cartInfoList.add(cartInfo);
        }
        // 先将集合转换成字符串
        String newCartJson  = JSON.toJSONString(cartInfoList);
        // 将cartInfo 信息放到cookie中 ，
        CookieUtil.setCookie(request,response,cookieCartName,newCartJson,COOKIE_CART_MAXAGE,true);

    }
    /*取得cookie中的信息*/
    public List<CartInfo> getCartList(HttpServletRequest request){
        // 调用工具类
        String cartJson  = CookieUtil.getCookieValue(request, cookieCartName, true);
        // 将字符串转换成对象
        List<CartInfo> cartInfoList = JSON.parseArray(cartJson, CartInfo.class);
        return cartInfoList;
    }
    /*删除cookie中的信息*/
    public void deleteCartCookie(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request,response,cookieCartName);
    }

    public void checkCart(HttpServletRequest request, HttpServletResponse response, String skuId, String isChecked) {
        // 取得所有值
        List<CartInfo> cartList = getCartList(request);
        // 循环遍历
        for (CartInfo cartInfo : cartList) {
            // 判断skuId 是否存在
            if (cartInfo.getSkuId().equals(skuId)){
                cartInfo.setIsChecked(isChecked);
            }
        }
        // 将最新的cookie数据放入到cookie
        CookieUtil.setCookie(request,response,cookieCartName,JSON.toJSONString(cartList),COOKIE_CART_MAXAGE,true);
    }
}
