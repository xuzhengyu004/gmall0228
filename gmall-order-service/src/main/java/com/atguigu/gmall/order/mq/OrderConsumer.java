package com.atguigu.gmall.order.mq;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.enums.ProcessStatus;
import com.atguigu.gmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;

@Component
public class OrderConsumer {

    // 消费！根据name消费信息，实际上是利用MessageListener监听器，不断的去扫描。如果有，则进行消费。

    @Autowired
    OrderService orderService;

    @JmsListener(destination = "PAYMENT_RESULT_QUEUE",containerFactory = "jmsQueueListener")
    public void  consumerPaymentResult(MapMessage mapMessage) throws JMSException {
        // {result=success, orderId=53}
        String orderId = mapMessage.getString("orderId");
        String result = mapMessage.getString("result");
        // 如果成功，则进行更新orderInfo的状态
        if ("success".equals(result)){
            // 成功的时候，消息不确定性！
            orderService.updateOrderStatus(orderId, ProcessStatus.PAID);
            // 准备通知库存，进行减库存的操作！
            // 先发送消息队列
            orderService.sendOrderStatus(orderId);
            // 更新订单的进度，变成等待发货 ,拼接字符串的时候：注意：OrderDetail 不能为空！
            orderService.updateOrderStatus(orderId,ProcessStatus.WAITING_DELEVER);
        }else {
            // 失败的时候，更新为未付款！
            orderService.updateOrderStatus(orderId,ProcessStatus.UNPAID);
        }
    }
    @JmsListener(destination = "SKU_DEDUCT_QUEUE",containerFactory = "jmsQueueListener")
    public void  consumeSkuDeduct(MapMessage mapMessage) throws JMSException {
        // {result=success, orderId=53}
        String orderId = mapMessage.getString("orderId");
        String status = mapMessage.getString("status");
        // 如果成功，则进行更新orderInfo的状态
        if ("DEDUCTED".equals(status)){
            orderService.updateOrderStatus(orderId,ProcessStatus.DELEVERED);
        }else {
            orderService.updateOrderStatus(orderId,ProcessStatus.STOCK_EXCEPTION);
        }
    }


}

//错误原因：底层源码产生active=true，出现死循环，以至于无法调用产生producer的方法，支付模块正常，是传入数据有问题？还是步骤有问题?