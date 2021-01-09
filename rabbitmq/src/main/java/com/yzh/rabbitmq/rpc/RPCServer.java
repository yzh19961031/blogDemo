package com.yzh.rabbitmq.rpc;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * RPC服务端
 *
 * @author yuanzhihao
 * @since 2020/11/21
 */
public class RPCServer {

    public static void main(String[] args) throws IOException, TimeoutException {
        // 首先还是正常获得connection以及channel对象
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.108");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        // 定义一个rpc的队列
        String queueName = "test_rpc";
        channel.queueDeclare(queueName, false, false, false, null);

        Object monitor = new Object();
        // 具体的消费代码里面实现
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            // 消费者将请求消息中的correlationId信息再作为响应传回replyTo队列
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();

            String response = "";
            try {
                // 提供一个大小写转换的方法
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("toUpperCase(" + message + ")");
                response = toUpperCase(message);
            } catch (RuntimeException e) {
                System.out.println(e.toString());
            } finally {
                // 将响应传回replyTo队列
                channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes(StandardCharsets.UTF_8));
                // 设置了手动应答 需要手动确认消息
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                // 执行完成会释放主线程的锁
                // RabbitMq consumer worker thread notifies the RPC server owner thread
                synchronized (monitor) {
                    monitor.notify();
                }
            }
        };

        // 监听"test_rpc"队列
        channel.basicConsume(queueName, false, deliverCallback, (consumerTag -> { }));
        // 这个锁对象是确保我们server的调用逻辑执行完成 首先挂起主线程
        // Wait and be prepared to consume the message from RPC client.
        while (true) {
            synchronized (monitor) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 提供一个大小写转换的方法
    private static String toUpperCase(String msg) {
        return msg.toUpperCase();
    }
}
