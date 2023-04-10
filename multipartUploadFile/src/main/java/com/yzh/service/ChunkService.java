package com.yzh.service;

import com.yzh.bean.Chunk;
import com.yzh.bean.FileInfo;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * 文件分块上传
 *
 * @author yuanzhihao
 * @since 2023/4/10
 */
public interface ChunkService {
    /**
     * 分块上传文件
     *
     * @param chunk 文件块信息
     */
    void chunk(Chunk chunk);

    /**
     * 文件合并
     *
     * @param filename 文件名
     */
    void merge(String filename);

    /**
     * 获取文件列表
     *
     * @return 文件列表
     */
    List<FileInfo> list();

    /**
     * 获取指定文件
     *
     * @param filename 文件名称
     * @return 文件
     */
    Resource getFile(String filename);
}
