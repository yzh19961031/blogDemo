import cn.hutool.core.io.FileUtil;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.RandomAccessFile;

/**
 * 模拟前端逻辑 在代码中实现大文件分片上传 使用restTemplate客户端
 *
 * @author yuanzhihao
 * @since 2023/4/10
 */
public class MultipartUploadTest {

    @Test
    public void testUpload() throws Exception {
        String chunkFileFolder = "/data/bucket/";
        java.io.File file = new java.io.File("/Users/yuanzhihao/Downloads/ideaIU-2022.2.2.exe");
        long contentLength = file.length();
        // 每块大小设置为20MB
        long partSize = 20 * 1024 * 1024;
        // 文件分片块数 最后一块大小可能小于 20MB
        long chunkFileNum = (long) Math.ceil(contentLength * 1.0 / partSize);
        RestTemplate restTemplate = new RestTemplate();

        try (RandomAccessFile raf_read = new RandomAccessFile(file, "r")) {
            // 缓冲区
            byte[] b = new byte[1024];
            for (int i = 1; i <= chunkFileNum; i++) {
                // 块文件
                java.io.File chunkFile = new java.io.File(chunkFileFolder + i);
                // 创建向块文件的写对象
                try (RandomAccessFile raf_write = new RandomAccessFile(chunkFile, "rw")) {
                    int len;
                    while ((len = raf_read.read(b)) != -1) {
                        raf_write.write(b, 0, len);
                        // 如果块文件的大小达到20M 开始写下一块儿  或者已经到了最后一块
                        if (chunkFile.length() >= partSize) {
                            break;
                        }
                    }
                    // 上传
                    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                    body.add("file", new FileSystemResource(chunkFile));
                    body.add("chunkNumber", i);
                    body.add("chunkSize", partSize);
                    body.add("currentChunkSize", chunkFile.length());
                    body.add("totalSize", contentLength);
                    body.add("filename", file.getName());
                    body.add("totalChunks", chunkFileNum);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
                    String serverUrl = "http://localhost:8080/chunk";
                    ResponseEntity<String> response = restTemplate.postForEntity(serverUrl, requestEntity, String.class);
                    System.out.println("Response code: " + response.getStatusCode() + " Response body: " + response.getBody());
                } finally {
                    FileUtil.del(chunkFile);
                }
            }
        }
        // 合并文件
        String mergeUrl = "http://localhost:8080/merge?filename=" + file.getName();
        ResponseEntity<String> response = restTemplate.getForEntity(mergeUrl, String.class);
        System.out.println("Response code: " + response.getStatusCode() + " Response body: " + response.getBody());
    }
}
