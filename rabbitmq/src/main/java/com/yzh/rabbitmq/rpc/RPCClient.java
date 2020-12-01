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
 *Ø
 * @author yuanzhihao
 * @since 2020/11/21
 */

public class RPCClient {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("192.168.1.108");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        try ( Connection connection = connectionFactory.newConnection();
              Channel channel = connection.createChannel()) {
            // 声明一个队列
            String queueName = "test_rpc";

            String corrId = UUID.randomUUID().toString();
            String replayQueueName = channel.queueDeclare().getQueue();
            AMQP.BasicProperties properties = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(corrId)
                    .replyTo(replayQueueName)
                    .build();
            String msg = "hello rpc";
            channel.basicPublish("",queueName,properties,msg.getBytes());
            final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

            String ctag = channel.basicConsume(replayQueueName, true, (consumerTag, message) -> {
                if (message.getProperties().getCorrelationId().equals(corrId)) {
                    response.offer(new String(message.getBody(), StandardCharsets.UTF_8));
                }
            }, consumerTag -> {
            });

            // 获取响应结果
            String take = response.take();
            System.out.println("rpc result is "+ take);
            channel.basicCancel(ctag);
        }
    }
}
