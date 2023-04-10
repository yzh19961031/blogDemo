package com.yzh.controller;

import com.yzh.bean.Chunk;
import com.yzh.bean.FileInfo;
import com.yzh.service.ChunkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分片上传
 *
 * @author yuanzhihao
 * @since 2023/4/10
 */
@RestController
@RequestMapping
public class ChunkController {
    @Autowired
    private ChunkService chunkService;

    /**
     * 分块上传文件
     *
     * @param chunk 文件块信息
     * @return 响应
     */
    @PostMapping(value = "chunk")
    public ResponseEntity<String> chunk(Chunk chunk) {
        chunkService.chunk(chunk);
        return ResponseEntity.ok("File Chunk Upload Success");
    }

    /**
     * 文件合并
     *
     * @param filename 文件名
     * @return 响应
     */
    @GetMapping(value = "merge")
    public ResponseEntity<Void> merge(@RequestParam("filename") String filename) {
        chunkService.merge(filename);
        return ResponseEntity.ok().build();
    }


    /**
     * 获取文件列表
     *
     * @return 文件列表
     */
    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> list() {
        return ResponseEntity.ok(chunkService.list());
    }

    /**
     * 获取指定文件
     *
     * @param filename 文件名称
     * @return 文件
     */
    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable("filename") String filename) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"").body(chunkService.getFile(filename));
    }
}
