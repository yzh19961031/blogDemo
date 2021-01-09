package com.yzh.rabbitmq.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

/**
 * 主配置类
 *
 * @author yuanzhihao
 * @since 2021/1/9
 */
@Configuration
public class RabbitMQConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    // 注入connectionFactory对象
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("192.168.1.108:5672");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        return connectionFactory;
    }

    // 声明队列
    @Bean
    public Queue rpcQueue() {
        return new Queue("test_rpc",false);
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    // 创建初始化RabbitAdmin对象
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        // 只有设置为 true，spring 才会加载 RabbitAdmin 这个类
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    // 消息监听器
    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(RabbitTemplate rabbitTemplate) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
        // 监听的队列
        container.setQueues(rpcQueue());
        MessageListener messageListener = message -> {
            String receiveMsg = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("Receive a message message is {}", receiveMsg);
            // 执行对应逻辑
            String responseMsg = toUpperCase(receiveMsg);
            MessageProperties messageProperties = MessagePropertiesBuilder.newInstance().
                    setCorrelationId(message.getMessageProperties().getCorrelationId()).
                    build();
            // 响应消息 这边就是如果没有绑定交换机和队列的话 消息应该直接传到对应的队列上面
            rabbitTemplate.send("", message.getMessageProperties().getReplyTo(), new Message(responseMsg.getBytes(StandardCharsets.UTF_8), messageProperties));
        };
        // 设置监听器
        container.setMessageListener(messageListener);
        return container;
    }

    // 提供一个大小写转换的方法
    private String toUpperCase(String msg) {
        return msg.toUpperCase();
    }
}
