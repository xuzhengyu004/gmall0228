package com.atguigu.gmall.payment.task;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.OrderInfo;
import com.atguigu.gmall.service.OrderService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

// 启动轮询
@EnableScheduling
@Component
public class OrderTask {


    @Reference
    private OrderService orderService;
//   每分钟的第五秒
//    @Scheduled(cron = "5 * * * * ?")
//    public void  sayHi(){
//        System.out.println("Thread ===="+Thread.currentThread());
//    }
//  每隔五秒执行
//    @Scheduled(cron = "0/5 * * * * ?")
//    public void  work(){
//        System.out.println("Thread1 ===="+Thread.currentThread());
//    }
//    每隔一段时间执行一次任务 定时扫描关闭过期的订单！什么样的订单应该关闭！UNPAID，expireTime 20 < 当前系统时间 21
    @Scheduled(cron = "0/20 * * * * ?")
    public void  checkOrder(){

//       获取过期订单
        List<OrderInfo> expiredOrderList = orderService.getExpiredOrderList();
        // 循环遍历过去订单，将订单准备更新状态
        for (OrderInfo orderInfo : expiredOrderList) {
            // 关闭订单 processStatus=Close！
            orderService.execExpiredOrder(orderInfo);
        }
    }



}
