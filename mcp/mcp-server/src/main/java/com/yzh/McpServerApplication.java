package com.yzh;


import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * mcp server 启动类
 *
 * @author yuanzhihao
 * @since 2025/4/25
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class McpServerApplication {

    public static void main(String[] args){
        SpringApplication.run(McpServerApplication.class, args);
    }


    @Bean
    public ToolCallbackProvider toolCallbackProvider(ToolService toolService) {
        return MethodToolCallbackProvider.builder().toolObjects(toolService).build();
    }
}
