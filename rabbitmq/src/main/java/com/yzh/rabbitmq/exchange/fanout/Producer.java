package com.yzh.rabbitmq.exchange.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息生产者
 *
 * @author yuanzhihao
 * @since 2020/11/21
 */
public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.108");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        try (//获得connection
             Connection connection = connectionFactory.newConnection();
             //获得channel对象
             Channel channel = connection.createChannel()) {
            String exchangeName = "test_topic_exchange";
            String routingKey = "user.add";
            String routingKey1 = "user.delete";
            String routingKey2 = "user.update.do";
            String msg = "test topic exchange";
            channel.basicPublish(exchangeName, routingKey, null, msg.getBytes());
            channel.basicPublish(exchangeName, routingKey1, null, msg.getBytes());
            channel.basicPublish(exchangeName, routingKey2, null, msg.getBytes());
        }
    }
}
