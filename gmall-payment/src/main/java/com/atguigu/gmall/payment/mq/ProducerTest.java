package com.atguigu.gmall.payment.mq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;

public class ProducerTest {
    public static void main(String[] args) throws JMSException {
        // 创建工厂
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory("tcp://192.168.176.130:61616");
        // 创建连接
        Connection connection = activeMQConnectionFactory.createConnection();
        // 启动
        connection.start();
        // 发布消息，producer，queue。
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // 创建一个队列
        Queue queue = session.createQueue("Atguigu");
        // 创建提供者
        MessageProducer producer = session.createProducer(queue);
        // 消息持久化
        //   producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        // 创建一个消息对象
        ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();
        activeMQTextMessage.setText("这么困呢？晚上干啥去了！");
        // 准备发送消息
        producer.send(activeMQTextMessage);
        // 关闭的时候，如果有事务开启，则必须先提交事务  session.commit();
        producer.close();
        session.close();
        connection.close();
    }
}
