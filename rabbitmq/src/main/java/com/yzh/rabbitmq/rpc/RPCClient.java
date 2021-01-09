package com.yzh.rabbitmq.rpc;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

/**
 * RPC客户端
 *
 * @author yuanzhihao
 * @since 2020/11/21
 */

public class RPCClient {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        // 创建connection以及channel对象
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.108");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        try ( Connection connection = connectionFactory.newConnection();
              Channel channel = connection.createChannel()) {
            // 声明一个队列
            String queueName = "test_rpc";

            // 请求消息中需要带一个唯一标识ID
            String corrId = UUID.randomUUID().toString();
            // 声明一个回调队列
            String replayQueueName = channel.queueDeclare().getQueue();
            // 将correlationId以及回调队列设置在消息的属性中
            AMQP.BasicProperties properties = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(replayQueueName)
                    .build();
            // 具体消息内容
            String msg = "hello rpc";
            // 发送请求消息
            channel.basicPublish("",queueName,properties,msg.getBytes());
            // 设置一个阻塞队列  等待服务端的响应
            final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

            String ctag = channel.basicConsume(replayQueueName, true, (consumerTag, message) -> {
                // 注意 这边根据correlationId进行下判断
                if (message.getProperties().getCorrelationId().equals(corrId)) {
                    response.offer(new String(message.getBody(), StandardCharsets.UTF_8));
                }
            }, consumerTag -> {});

            // 获取响应结果
            String take = response.take();
            System.out.println("rpc result is "+ take);
            channel.basicCancel(ctag);
        }
    }
}
