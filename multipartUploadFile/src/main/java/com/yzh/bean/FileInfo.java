package com.yzh.bean;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 文件属性
 *
 * @author yuanzhihao
 * @since 2023/3/28
 */
@Data
@Accessors(chain = true)
public class FileInfo {
    private String filename;
    private String uploadTime;
}
