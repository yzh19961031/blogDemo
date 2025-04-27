package com.yzh.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ssh实体信息
 *
 * @author yuanzhihao
 * @since 2025/4/25
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SshEntity {
    private String host;
    private String username;
    private String password;
    private Integer port;
}
