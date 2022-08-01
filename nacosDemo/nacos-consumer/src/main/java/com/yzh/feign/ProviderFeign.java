package com.yzh.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 配置生产者provider
 *
 * @author yuanzhihao
 * @since 2022/8/1
 */
@FeignClient(value = "nacos-provider")
public interface ProviderFeign {

    @GetMapping("/hello/{name}")
    String hello(@PathVariable("name") String name);
}
