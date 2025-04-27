package com.yzh.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * mysql实体
 *
 * @author yuanzhihao
 * @since 2025/4/25
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MysqlEntity {
    private String host;
    private Integer port;
    private String username;
    private String password;
}
