package com.yzh;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * nacos config
 *
 * @author yuanzhihao
 * @since 2022/8/1
 */
@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class NacosConfig1Application {
    @Value("${nacos.name}")
    private String name;

    @GetMapping("/name")
    public String name() {
        return "name is " + name;
    }

    public static void main(String[] args) {
        SpringApplication.run(NacosConfig1Application.class, args);
    }
}
