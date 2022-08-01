package com.yzh;

import com.yzh.feign.ProviderFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

/**
 * 消费者
 *
 * @author yuanzhihao
 * @since 2022/8/1
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("com.yzh.feign")
public class NacosConsumerApplication {

    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(NacosConsumerApplication.class, args);
    }

    @RestController
    public static class HelloController {
        @Autowired
        private ProviderFeign providerFeign;

        @Autowired
        private RestTemplate restTemplate;

        @GetMapping("/hello/{name}")
        public String hello(@PathVariable("name") String name) {
            return restTemplate.getForObject(String.format(Locale.ROOT, "http://nacos-provider/hello/%s", name), String.class);
        }

        @GetMapping("/hello2/{name}")
        public String hello2(@PathVariable("name") String name) {
            return providerFeign.hello(name);
        }
    }
}
