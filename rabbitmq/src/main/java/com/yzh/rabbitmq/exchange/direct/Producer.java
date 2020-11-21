package com.yzh.rabbitmq.exchange.direct;

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
            String exchangeName = "test_direct_exchange";
            String routingKey = "test_direct";
            String msg = "test direct queue";
            channel.basicPublish(exchangeName, routingKey, null, msg.getBytes());
        }
    }
}
