package com.yzh.rabbitmq.exchange.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息消费者 广播
 * 不需要routingKey路由规则 会将消息路由到所有与交换机绑定的队列上面
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

        String exchangeName = "test_fanout_exchange";
        String exchangeType = "fanout";
        String routingKey = ""; //fanout不需要路由规则  消息会到所有与交换机绑定的队列上
        String queueName = "test_fanout_queue";

        //声明交换机
        channel.exchangeDeclare(exchangeName, exchangeType, true, true, null);
        //声明队列
        channel.queueDeclare(queueName, false, false, false, null);
        //将交换机与队列进行绑定
        channel.queueBind(queueName, exchangeName, routingKey);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
}
