package com.yzh.controller;

import com.yzh.bean.FileInfo;
import com.yzh.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传
 *
 * @author yuanzhihao
 * @since 2023/3/22
 */
@RestController
@RequestMapping
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;


    /**
     * 上传文件
     *
     * @param files 文件
     * @return 响应消息
     */
    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("files") MultipartFile[] files) {
        fileUploadService.upload(files);
        return ResponseEntity.ok("File Upload Success");
    }

    /**
     * 获取文件列表
     *
     * @return 文件列表
     */
    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> list() {
        return ResponseEntity.ok(fileUploadService.list());
    }

    /**
     * 获取指定文件
     *
     * @param fileName 文件名称
     * @return 文件
     */
    @GetMapping("/files/{fileName:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable("fileName") String fileName) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileName + "\"").body(fileUploadService.getFile(fileName));
    }
}
