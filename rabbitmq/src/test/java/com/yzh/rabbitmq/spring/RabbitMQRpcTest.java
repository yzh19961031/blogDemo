package com.yzh.rabbitmq.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * spring amqp rpc 测试类
 *
 * @author yuanzhihao
 * @since 2021/1/9
 */
@ContextConfiguration(classes = {RabbitMQConfig.class})
@RunWith(SpringRunner.class)
public class RabbitMQRpcTest {
    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 测试RPC客户端
    @Test
    public void testRpcClient() {
        // 设置correlationId
        String corrId = UUID.randomUUID().toString();
        String msg = "hello rpc";
        MessageProperties messageProperties = MessagePropertiesBuilder.newInstance().setCorrelationId(corrId).build();
        // 注意 这边如果使用sendAndReceive不指定replyTo回调队列 spring会默认帮我们添加一个回调队列
        // 格式默认 "amq.rabbitmq.reply-to" 前缀
        Message message = rabbitTemplate.sendAndReceive("", "test_rpc", new Message(msg.getBytes(StandardCharsets.UTF_8), messageProperties));
        assert message != null;
        log.info("The response is {}", new String(message.getBody(), StandardCharsets.UTF_8));

    }
}
