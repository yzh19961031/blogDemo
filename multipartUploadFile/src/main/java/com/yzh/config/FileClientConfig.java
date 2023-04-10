package com.yzh.config;

import com.yzh.service.FileClient;
import com.yzh.service.impl.AWSFileClient;
import com.yzh.service.impl.LocalFileSystemClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 文件上传客户端 通过配置文件指定加载哪个客户端
 * 目前支持本地文件和AWS S3对象存储
 *
 * @author yuanzhihao
 * @since 2023/4/10
 */
@Configuration
public class FileClientConfig {
    @Value("${file.client.type:local-file}")
    private String fileClientType;

    private static final Map<String, Supplier<FileClient>> FILE_CLIENT_SUPPLY = new HashMap<String, Supplier<FileClient>>() {
        {
            put("local-file", LocalFileSystemClient::new);
            put("aws-s3", AWSFileClient::new);
        }
    };

    /**
     * 注入文件客户端对象
     *
     * @return 文件客户端
     */
    @Bean
    public FileClient fileClient() {
        return FILE_CLIENT_SUPPLY.get(fileClientType).get();
    }
}
