package yzh;

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
public class NacosConfig2Application {
    @Value("${nacos.name}")
    private String name;

    @Value("${shared1.name}")
    private String shared1Name;

    @Value("${shared2.name}")
    private String shared2Name;

    @GetMapping("/name")
    public String name() {
        return "name is " + name + ";  shared1Name is " + shared1Name + ";  shared2Name is " + shared2Name;
    }

    public static void main(String[] args) {
        SpringApplication.run(NacosConfig2Application.class, args);
    }
}
