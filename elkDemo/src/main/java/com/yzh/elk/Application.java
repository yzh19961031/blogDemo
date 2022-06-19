package com.yzh.elk;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yuanzhihao
 * @since 2022/6/19
 */
@SpringBootApplication
@RestController
@Slf4j
public class Application {
    private static final Logger operationLog = LoggerFactory.getLogger("operation");
    private static final Logger securityLog = LoggerFactory.getLogger("security");

    @GetMapping("/hello")
    public String hello() {
        log.info("hello info log");
        operationLog.info("hello operation log");
        securityLog.info("hello security log");
        return "Hello elk demo";
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
