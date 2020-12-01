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
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.108");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        //获得connection
        Connection connection = connectionFactory.newConnection();
        //获得channel对象
        Channel channel = connection.createChannel();

        String queueName = "test_rpc";
        channel.queueDeclare(queueName, false, false, false, null);


        Object monitor = new Object();
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();

            String response = "";
            try {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("toUpperCase(" + message + ")");
                response = toUpperCase(message);
            } catch (RuntimeException e) {
                System.out.println(e.toString());
            } finally {
                channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes(StandardCharsets.UTF_8));
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                // RabbitMq consumer worker thread notifies the RPC server owner thread
                synchronized (monitor) {
                    monitor.notify();
                }
            }
        };

        channel.basicConsume(queueName, false, deliverCallback, (consumerTag -> { }));
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
