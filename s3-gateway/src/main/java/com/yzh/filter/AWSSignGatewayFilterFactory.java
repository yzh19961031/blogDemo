package com.yzh.filter;

import com.amazonaws.DefaultRequest;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.internal.S3Signer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 重新生成S3签名过滤器
 *
 * @author yuanzhihao
 * @since 2023/5/5
 */
@Component
@Slf4j
public class AWSSignGatewayFilterFactory extends AbstractGatewayFilterFactory<AWSSignGatewayFilterFactory.Config> {

    public AWSSignGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            DefaultRequest<Void> defaultRequest = regenerateAuthorization(config, exchange);
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .headers(httpHeaders -> {
                        httpHeaders.set("Authorization", defaultRequest.getHeaders().get("Authorization"));
                        httpHeaders.set(Headers.DATE, defaultRequest.getHeaders().get(Headers.DATE));
                    })
                    .build();
            return chain.filter(exchange.mutate().request(request).build());
        };
    }


    // 重新计算并设置签名
    private DefaultRequest<Void> regenerateAuthorization(Config config, ServerWebExchange exchange) {
        AWSCredentials credentials = new BasicAWSCredentials(config.getAk(), config.getSk());
        DefaultRequest<Void> request = new DefaultRequest<>("Amazon S3");
        request.addHeader("Host", config.getEndpoint());
        // 这边把请求头全部带下去
        exchange.getRequest().getQueryParams().forEach((key, value) -> request.addParameter(key, value.get(0)));
        exchange.getRequest().getHeaders().forEach((key, value) -> request.addHeader(key, value.get(0)));
        String path = exchange.getRequest().getURI().getPath();
        String method = Objects.requireNonNull(exchange.getRequest().getMethod(), "Method is null").toString();
        request.setResourcePath(path);
        try {
            request.setEndpoint(new URI(config.getEndpoint()));
        } catch (URISyntaxException e) {
            log.error("URI error", e);
            throw new RuntimeException(e);
        }
        S3Signer signer = new S3Signer(method, path);
        signer.sign(request, credentials);
        return request;
    }


    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("endpoint", "ak", "sk");
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Config {
        private String endpoint;
        private String ak;
        private String sk;
    }
}
