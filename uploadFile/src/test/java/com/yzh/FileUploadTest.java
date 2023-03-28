package com.yzh;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author yuanzhihao
 * @since 2023/3/22
 */
public class FileUploadTest {

    @Test
    public void uploadTestByRestTemplate() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        File file = new File("/Users/yuanzhihao/Downloads/mirrors-jenkins-master.zip");
        body.add("files", new FileSystemResource(file));
        body.add("files", new FileSystemResource(new File("/Users/yuanzhihao/Downloads/crictl-v1.22.0-linux-amd64.tar.gz")));
        body.add("files", new FileSystemResource(new File("/Users/yuanzhihao/Downloads/client(macosx).zip")));
        body.add("files", new FileSystemResource(new File("/Users/yuanzhihao/Downloads/FileZilla_3.62.2_macosx-x86.app.tar.bz2")));
        body.add("files", new FileSystemResource(new File("/Users/yuanzhihao/Downloads/sequelpro_mac_v1.1.2的副本.dmg")));
        body.add("files", new FileSystemResource(new File("/Users/yuanzhihao/Downloads/sequelpro_mac_v1.1.2的副本2.dmg")));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String serverUrl = "http://localhost:8080/upload";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(serverUrl, requestEntity, String.class);
        System.out.println("Response code: " + response.getStatusCode() + " Response body: " + response.getBody());
    }


    @Test
    public void uploadTestByHttpClient() {
        File file = new File("/Users/yuanzhihao/Downloads/xzs-sql-v3.9.0.zip");
        FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addPart("files", fileBody);

        HttpPost post = new HttpPost("http://localhost:8080/upload");
        org.apache.http.HttpEntity entity = builder.build();

        post.setEntity(entity);
        try (CloseableHttpClient client = HttpClientBuilder.create().build();
            CloseableHttpResponse response = client.execute(post)) {
            System.out.println("Response code: " + response.getStatusLine().getStatusCode());
            System.out.println("Response body: " + EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
