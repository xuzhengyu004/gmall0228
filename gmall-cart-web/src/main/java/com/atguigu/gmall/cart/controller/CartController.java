package com.atguigu.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.config.LoginRequire;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class CartController {

    @Reference
    private CartService cartService;

    @Reference
    private ManageService manageService;

    @Autowired
    private  CartCookieHandler cartCookieHandler;

    // 走cookie的对象
    @RequestMapping(value = "addToCart",method = RequestMethod.POST)
    @LoginRequire(autoRedirect = false)
    public String addToCart(HttpServletRequest request, HttpServletResponse response,Model model){
        // 先判断CartInfo中是否有该商品，有则数据加1，放到缓存，没有从新创建，skuId,userId【sso中取得request.getAttribute("userId");】.
        // 取得userId,skuId,skuNum
        String skuNum = request.getParameter("skuNum");
        String skuId = request.getParameter("skuId");
        String userId = (String) request.getAttribute("userId");

        // 判断用户是否登录
        if (userId!=null){
            // 走数据库
            cartService.addToCart(skuId,userId,Integer.parseInt(skuNum));
        }else{
            // 走cookie
            cartCookieHandler.addToCart(request,response,skuId,userId,Integer.parseInt(skuNum));
        }

        // 获取skuInfo 信息 返回给成功的页面，后台存储什么，是根据前台需要！
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);

        // 存储skuInfo 对象，
        model.addAttribute("skuInfo",skuInfo);
        model.addAttribute("skuNum",skuNum);
        return "success";
    }


        /*显示购物车中的信息*/
        @RequestMapping("cartList")
        @LoginRequire(autoRedirect = false)
        public String cartList(HttpServletRequest request, HttpServletResponse response,Model model){
            // 判断是否登录
            String userId = (String) request.getAttribute("userId");
            // 取得cookie中所有的cartInfo 数据
            List<CartInfo> cartListFromCookie = cartCookieHandler.getCartList(request);
            List<CartInfo> cartList = null;
            if (userId!=null){
                // cookie + mysql
                if (cartListFromCookie!=null && cartListFromCookie.size()>0){
                    // 合并购物车，cookie-->db。 根据skuId 相同的就合并，合并完之后，返回一个集合
                    cartList = cartService.mergeToCartList(cartListFromCookie, userId);
                    // cookie删除掉。
                    cartCookieHandler.deleteCartCookie(request,response);
                }else{
                    // 走的数据库！
                    cartList = cartService.getCartList(userId);
                }

                // 将集合保存给前台使用
                model.addAttribute("cartList",cartList);
            }else{
                // 没有登录，cookie中取得
                List<CartInfo> cookieHandlerCartList = cartCookieHandler.getCartList(request);
                model.addAttribute("cartList",cookieHandlerCartList);
            }
            return "cartList";
        }

    // 参数：看内部需要使用那些方法，cookie，cartService. String isChecked , CartInfo
    @RequestMapping(value = "checkCart",method = RequestMethod.POST)
    @LoginRequire(autoRedirect = false)
    @ResponseBody
    public void checkCart(HttpServletRequest request,HttpServletResponse response){
        // 取得userId判断是否登录
        String userId = (String) request.getAttribute("userId");
        String isChecked = request.getParameter("isChecked");
        String skuId = request.getParameter("skuId");
        if (userId!=null){
            // 登录 ,将数据取出来，操作redis === 封装成一个方法 ,db
            cartService.checkCart(userId,skuId,isChecked);
        }else {
            // 未登录,从cookie中取出数据
            cartCookieHandler.checkCart(request,response,skuId,isChecked);
        }
    }

    /*结算跳转页面*/
    @RequestMapping("toTrade")
    @LoginRequire(autoRedirect = true)
    public String toTrade(HttpServletRequest request,HttpServletResponse response){
        // 取得userId
        String userId = (String) request.getAttribute("userId");
        // 结算的时候：cookie+db
        List<CartInfo> cookieHandlerCartList = cartCookieHandler.getCartList(request);

        // 循环遍历cookie中的值，跟db进行合并
        if (cookieHandlerCartList!=null && cookieHandlerCartList.size()>0){
            // 准备合并
            cartService.mergeToCartList(cookieHandlerCartList,userId);
            // 将cookie中的数据删除！
            cartCookieHandler.deleteCartCookie(request,response);
        }
        return "redirect://order.gmall.com/trade";
    }





}
