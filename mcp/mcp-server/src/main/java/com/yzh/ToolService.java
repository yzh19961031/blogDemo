package com.yzh;


import cn.hutool.extra.ssh.JschUtil;
import com.jcraft.jsch.Session;
import com.yzh.model.MysqlEntity;
import com.yzh.model.SshEntity;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 提供Function Calling
 *
 * @author yuanzhihao
 * @since 2025/4/25
 */
@Service
public class ToolService {

    // 模拟业务查询
    private static final Map<String, SshEntity> SSH_STORE = Map.of();

    private static final Map<String, MysqlEntity> MYSQL_STORE = Map.of();


    @Tool(name = "执行shell命令并且获取输出")
    public String execCommand(String host, String commands) {
        SshEntity sshEntity = SSH_STORE.get(host);
        if (Objects.isNull(sshEntity)) {
            throw new IllegalArgumentException("主机不存在！！！");
        }
        Session session = JschUtil.getSession(host, sshEntity.getPort(), sshEntity.getUsername(), sshEntity.getPassword());
        return JschUtil.exec(session, commands, StandardCharsets.UTF_8);
    }


    @Tool(name = "执行mysql命令并且获取输出")
    public List<Map<String, Object>> execMysql(String host, String database, String sqlStatement) {
        MysqlEntity mysqlEntity = MYSQL_STORE.get(host);
        if (Objects.isNull(mysqlEntity)) {
            throw new IllegalArgumentException("Mysql主机不存在！！！");
        }
        JdbcTemplate jdbcTemplate = buildJdbcTemplate(mysqlEntity, database);

        try (HikariDataSource ignored = (HikariDataSource) jdbcTemplate.getDataSource()) {
            return jdbcTemplate.queryForList(sqlStatement);
        }
    }


    @Tool(name = "执行命令，不需要返回，比如kill某一个进程")
    public void execMysqlCommand(String host, String database, String command) {
        MysqlEntity mysqlEntity = MYSQL_STORE.get(host);
        if (Objects.isNull(mysqlEntity)) {
            throw new IllegalArgumentException("Mysql主机不存在！！！");
        }
        JdbcTemplate jdbcTemplate = buildJdbcTemplate(mysqlEntity, database);

        try (HikariDataSource ignored = (HikariDataSource) jdbcTemplate.getDataSource()) {
            jdbcTemplate.update(command);
        }
    }


    private JdbcTemplate buildJdbcTemplate(MysqlEntity mysqlEntity, String database) {
        HikariConfig config = new HikariConfig();
        String jdbcUrl = "jdbc:mysql://" + mysqlEntity.getHost() + ":" + mysqlEntity.getPort() + "/" +
                database + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&characterEncoding=utf8";

        config.setJdbcUrl(jdbcUrl);
        config.setUsername(mysqlEntity.getUsername());
        config.setPassword(mysqlEntity.getPassword());
        config.setMaximumPoolSize(2); // 设置最大连接数
        config.setMinimumIdle(1);     // 设置最小空闲连接数
        config.setIdleTimeout(30000); // 设置空闲超时
        config.setConnectionTimeout(30000); // 设置连接超时
        config.setMaxLifetime(1800000);// 设置连接最大生命周期
        HikariDataSource hikariDataSource = new HikariDataSource(config);
        return new JdbcTemplate(hikariDataSource);
    }
}
