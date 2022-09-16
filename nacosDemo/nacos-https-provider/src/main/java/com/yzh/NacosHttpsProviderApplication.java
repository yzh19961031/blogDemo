package com.yzh;

import com.alibaba.nacos.common.tls.TlsSystemConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yuanzhihao
 * @since 2022/9/8
 */
@SpringBootApplication
@EnableDiscoveryClient
@Slf4j
public class NacosHttpsProviderApplication {
    public static void main(String[] args) {
        loadNacosCertificate();
        SpringApplication.run(NacosHttpsProviderApplication.class, args);
    }


    @RestController
    public static class HelloController {
        @GetMapping("/hello/{name}")
        public String hello(@PathVariable("name")String name) {
            return "hello " + name;
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
