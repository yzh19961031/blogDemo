package com.yzh.rabbitmq.exchange.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;


import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息消费者 直连交换机
 * 消息会根据routingKey路由到指定的队列上面
 *
 * @author yuanzhihao
 * @since 2020/11/21
 */
public class Consumer {

    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.108");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        //获得connection
        Connection connection = connectionFactory.newConnection();
        //获得channel对象
        Channel channel = connection.createChannel();
        String exchangeName = "test_direct_exchange";
        String exchangeType = "direct";
        String routingKey = "test_direct";
        String queueName = "test_direct_queue";

        //声明交换机
        channel.exchangeDeclare(exchangeName, exchangeType);
        //声明队列
        channel.queueDeclare(queueName, false, false, false, null);

        channel.queueBind(queueName, exchangeName, routingKey);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
}
