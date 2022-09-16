package com.yzh;

import com.alibaba.nacos.common.tls.TlsSystemConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

/**
 * 支持https
 *
 * @author yuanzhihao
 * @since 2022/9/8
 */
@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
public class NacosHttpsConsumerApplication {
    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        loadNacosCertificate();
        SpringApplication.run(NacosHttpsConsumerApplication.class, args);
    }

    @RestController
    public static class HelloController {
        @Autowired
        private RestTemplate restTemplate;

        @GetMapping("/hello/{name}")
        public String hello(@PathVariable("name") String name) {
            return restTemplate.getForObject(String.format(Locale.ROOT, "http://nacos-https-provider/hello/%s", name), String.class);
        }
    }

    // 加载nacos证书
    private static void loadNacosCertificate() {
        System.setProperty(TlsSystemConfig.TLS_ENABLE, "true");
        System.setProperty(TlsSystemConfig.CLIENT_AUTH, "true");
        try {
            System.setProperty(TlsSystemConfig.CLIENT_TRUST_CERT, ResourceUtils.getFile("classpath:nacosServer.cer").getCanonicalPath());
        } catch (Exception e) {
            log.error("Init nacosServer cer error", e);
        }
    }
}
