package com.yzh;


import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * mcp client 启动类
 *
 * @author yuanzhihao
 * @since 2025/4/25
 */
@RestController
@SpringBootApplication
public class McpClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpClientApplication.class, args);
    }


    @Resource
    ChatClient.Builder chatClientBuilder;

    /**
     * 工具回调提供
     */
    @Resource
    SyncMcpToolCallbackProvider toolCallbackProvider;


    private ChatClient chatClient;


    @GetMapping
    public String request(@RequestParam("question") String question) {
        if (Objects.isNull(chatClient)) {
            this.chatClient = chatClientBuilder
                    .defaultTools(toolCallbackProvider)
                    .build();
        }
        return chatClient.prompt(question).call().content();
    }
}
