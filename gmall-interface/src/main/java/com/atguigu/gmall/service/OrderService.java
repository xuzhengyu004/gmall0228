package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.OrderInfo;
import com.atguigu.gmall.bean.enums.ProcessStatus;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public interface OrderService {
    // 保存订单信息
    String  saveOrder(OrderInfo orderInfo);
    // 生成流水号
    String getTradeNo(String userId);
    // 检查流水号
    boolean checkTradeCode(String tradeNo,String userId);
    // 删除流水号
    void delTradeNo(String userId);
    // 检查库存
    boolean checkStock(String skuId, Integer skuNum);

    /*根据订单的ID查询订单的信息*/
    OrderInfo getOrderInfo(String orderId);
    /*更新orderInfo*/
    void updateOrderStatus(String orderId, ProcessStatus processStatus);
    /*通知库存准备发货*/
    void sendOrderStatus(String orderId);
    // 查询过期订单
    public List<OrderInfo> getExpiredOrderList();
    // 关闭订单
    void execExpiredOrder(OrderInfo orderInfo);
    // 将一个orderInfo 对象转换成map集合
    public Map initWareOrder (OrderInfo orderInfo);
    // 返回所有子订单的集合
    List<OrderInfo> orderSplit(String orderId, String wareSkuMap) throws InvocationTargetException, IllegalAccessException;


}
