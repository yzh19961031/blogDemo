package com.yzh.service.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.yzh.bean.Chunk;
import com.yzh.bean.ChunkProcess;
import com.yzh.service.FileClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AWS S3对象存储
 *
 * @author yuanzhihao
 * @since 2023/4/10
 */
@Slf4j
public class AWSFileClient implements FileClient {
    // 默认桶
    private static final String DEFAULT_BUCKET = "";

    private static final String AK = "";
    private static final String SK = "";
    private static final String ENDPOINT = "";
    private AmazonS3 s3Client;

    public AWSFileClient() {
        // 初始化文件客户端
        this.initFileClient();
    }

    @Override
    public void initFileClient() {
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(AK, SK)))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(ENDPOINT, "cn-north-1"))
                .build();
    }


    @Override
    public String initTask(String filename) {
        // 初始化分片上传任务
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(DEFAULT_BUCKET, filename);
        InitiateMultipartUploadResult initResponse = s3Client.initiateMultipartUpload(initRequest);
        return initResponse.getUploadId();
    }

    @Override
    public String chunk(Chunk chunk, String uploadId) {
        try (InputStream in = chunk.getFile().getInputStream()) {
            // 上传
            UploadPartRequest uploadRequest = new UploadPartRequest()
                    .withBucketName(DEFAULT_BUCKET)
                    .withKey(chunk.getFilename())
                    .withUploadId(uploadId)
                    .withInputStream(in)
                    .withPartNumber(chunk.getChunkNumber())
                    .withPartSize(chunk.getCurrentChunkSize());
            UploadPartResult uploadResult = s3Client.uploadPart(uploadRequest);
            return uploadResult.getETag();
        } catch (IOException e) {
            log.error("文件【{}】上传分片【{}】失败", chunk.getFilename(), chunk.getChunkNumber(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void merge(ChunkProcess chunkProcess) {
        List<PartETag> partETagList = chunkProcess.getChunkList()
                .stream()
                .map(chunkPart -> new PartETag(chunkPart.getChunkNumber(), chunkPart.getLocation()))
                .collect(Collectors.toList());
        CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(DEFAULT_BUCKET, chunkProcess.getFilename(),
                chunkProcess.getUploadId(), partETagList);
        s3Client.completeMultipartUpload(compRequest);
    }

    @Override
    public Resource getFile(String filename) {
        GetObjectRequest request = new GetObjectRequest(DEFAULT_BUCKET, filename);
        S3Object s3Object = s3Client.getObject(request);
        return new InputStreamResource(s3Object.getObjectContent());
    }
}
