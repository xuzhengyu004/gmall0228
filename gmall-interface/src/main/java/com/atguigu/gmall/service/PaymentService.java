package com.atguigu.gmall.service;

import com.alipay.api.AlipayApiException;
import com.atguigu.gmall.bean.PaymentInfo;

public interface PaymentService {

    /*保存信息*/
    void  savePaymentInfo(PaymentInfo paymentInfo);
    // 修改方法
    void updatePaymentInfo(PaymentInfo paymentInfo,String out_trade_no);
    // 根据out_trade_no 查询paymentInfo 对象
    PaymentInfo getpaymentInfo(PaymentInfo paymentInfo);
    //支付结果通知方法
    void sendPaymentResult(PaymentInfo paymentInfo,String result);
    /*检查是否支付成功*/
    public boolean checkPayment(PaymentInfo paymentInfoQuery) throws AlipayApiException;
    // 设置延迟队列！
    public void sendDelayPaymentResult(String outTradeNo,int delaySec ,int checkCount);
    // 关闭交易记录信息
    void closePayment(String id);

}
