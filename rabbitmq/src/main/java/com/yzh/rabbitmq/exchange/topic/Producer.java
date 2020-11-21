package com.yzh.rabbitmq.exchange.topic;

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
            String exchangeName = "test_fanout_exchange";
            String rountingKey = "";//fanout不需要路由规则  消息会到所有与交换机绑定的队列上
            String msg = "test fanout exchange";
            for (int i = 0; i < 5; i++) {
                channel.basicPublish(exchangeName,rountingKey, null, msg.getBytes());
            }
        }
    }
}
