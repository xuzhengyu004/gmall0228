package com.atguigu.gmall.payment.mq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;

public class ConsumerTest {

    public static void main(String[] args) throws JMSException {
        // 创建工厂,username,password,url.
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, "tcp://192.168.176.130:61616");
        // 创建连接
        Connection connection = activeMQConnectionFactory.createConnection();
        // 启动
        connection.start();
        // 创建session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // 创建队列
        Queue queue = session.createQueue("Atguigu");
        // 创建消费者
        MessageConsumer consumer = session.createConsumer(queue);
        // 消费者接受消息 ,
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                // 转换对象
                if (message instanceof TextMessage){
                    try {
                        String text = ((TextMessage) message).getText();
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        consumer.close();
        session.close();
        connection.close();
    }
}
