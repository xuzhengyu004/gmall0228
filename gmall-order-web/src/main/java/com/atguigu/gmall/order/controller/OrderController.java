package com.atguigu.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.bean.OrderDetail;
import com.atguigu.gmall.bean.OrderInfo;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.bean.enums.OrderStatus;
import com.atguigu.gmall.bean.enums.ProcessStatus;
import com.atguigu.gmall.config.LoginRequire;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.service.UserAddressService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    // 调用service 层 服务 @Autowired 不要了！
    @Reference
    private UserAddressService userAddressService;

    @Reference
    private CartService cartService;

    @Reference
    private OrderService orderService;
    
//    @RequestMapping("trade")
//    public List<UserAddress> trade(HttpServletRequest request) {
//        String userid = request.getParameter("userId");
//        List<UserAddress> userAddressList = userAddressService.getUserAddressList(userid);
//        return userAddressList;
//    }
//

    @RequestMapping("trade")
    @LoginRequire
    public String tradeInit(HttpServletRequest request, Model model){
        //获取userId
        String userId = (String) request.getAttribute("userId");
        //得到购物车的列表，从redis中勾选的key中取得数据
        List<CartInfo> cartInfoList = cartService.getCartCheckedList(userId);
        //收货人的地址
        List<UserAddress> userAddressList = userAddressService.getUserAddressList(userId);
        model.addAttribute("addressList",userAddressList);
        //订单详情的数据是从cartInfo对象中的来的，所以需要遍历数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (CartInfo cartInfo : cartInfoList) {
            //创建OrderDetail的对象
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            orderDetail.setOrderPrice(cartInfo.getCartPrice());
            orderDetail.setImgUrl(cartInfo.getImgUrl());

            orderDetailList.add(orderDetail);
        }
        model.addAttribute("orderDetailList",orderDetailList);
        /*数据展示，OrderInfo，OrderDetail
        * 保存信息，给前台显示
        * */
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderDetailList(orderDetailList);
        orderInfo.sumTotalAmount();
        model.addAttribute("totalAmount",orderInfo.getTotalAmount());

        // 保存流水号，给前台
        String tradeNo = orderService.getTradeNo(userId);
        model.addAttribute("tradeCode",tradeNo);
        // 数据库插入。点击提交订单的时候，插入数据库！


        return "trade";
    }
    /*提交订单*/
    @RequestMapping(value = "submitOrder",method = RequestMethod.POST)
    @LoginRequire
    public String submitOrder(HttpServletRequest request,OrderInfo orderInfo,Model model){
        // 取得userId
        String userId = (String) request.getAttribute("userId");


        // 防止重复提交
        String tradeNo = request.getParameter("tradeNo");
        boolean flag = orderService.checkTradeCode(tradeNo, userId);
        if (!flag){
            model.addAttribute("errMsg","提交订单失败，请联系管理员！");
            return "tradeFail";
        }

        // 验证库存就是在验DetailList
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            boolean result = orderService.checkStock(orderDetail.getSkuId(), orderDetail.getSkuNum());
            if (!result){
                model.addAttribute("errMsg","库存不足，请重新下单！");
                return "tradeFail";
            }
        }




        // orderInfo 表中有一些固定的字段值需要设置
        orderInfo.setOrderStatus(OrderStatus.UNPAID);
        orderInfo.setProcessStatus(ProcessStatus.UNPAID);

        orderInfo.setUserId(userId);
        // 计算的过程
        orderInfo.sumTotalAmount();
        orderInfo.setTotalAmount(orderInfo.getTotalAmount());

        // 调用service 中的方法 保存数据
        String orderId = orderService.saveOrder(orderInfo);
        // 删除redis 中的tradeNo
        orderService.delTradeNo(userId);
        /*支付的接口*/
        return "redirect://payment.gmall.com/index?orderId="+orderId;
    }

    // 拆单控制器 Json字符串！
    @RequestMapping(value = "orderSplit",method = RequestMethod.POST)
    @ResponseBody
    public String orderSplit(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        // 根据orderId ，进行拆单 库存系统！
        String orderId = request.getParameter("orderId");
        // 返回的Json数据[{"wareId":"1","skuIds":["2","10"]},{"wareId":"2","skuIds":["3"]}]
        String wareSkuMap = request.getParameter("wareSkuMap");
        // 服务层= 将根据orderId 传递过来的参数，对orderInfo订单信息进行拆单工作.
        List<OrderInfo> orderInfo = orderService.orderSplit(orderId,wareSkuMap);
        List<Map> orderDetailList = new ArrayList<>();

        // 从订单中取得子订单 一个订单中有很多子订单
        for (OrderInfo info : orderInfo) {
            // 获取json字符串 Map = 返回值例
            Map map = orderService.initWareOrder(info);
            orderDetailList.add(map);
        }
        // 将一个集合转换成字符串OrderDetail集合！
        return JSON.toJSONString(orderDetailList);

    }
}
