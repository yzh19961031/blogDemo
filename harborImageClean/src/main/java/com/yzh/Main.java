package com.yzh;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 定时扫描harbor中的项目 只保留最近10次构建的镜像tag
 *
 * @author yuanzhihao
 * @since 2022/6/29
 */
@Slf4j
public class Main {
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "Harbor12345";
    // 默认保存镜像的最大数量
    private static final int DEFAULT_REMAIN_ARTIFACT_COUNT = 10;
    // 需要清理的项目列表
    private static final List<String> PROJECTS = Arrays.asList("common", "library");
    private static final String GET_PROJECTS_INFO_URL = "http://192.168.1.103/api/v2.0/projects/%s";
    private static final String GET_REPOSITORIES_URL = "http://192.168.1.103/api/v2.0/projects/%s/repositories?page_size=%s";
    // 根据镜像push时间降序
    private static final String GET_ARTIFACTS_URL = "http://192.168.1.103/api/v2.0/projects/%s/repositories/%s/artifacts?page_size=%s&sort=-push_time";
    private static final String DELETE_ARTIFACTS_URL = "http://192.168.1.103/api/v2.0/projects/%s/repositories/%s/artifacts/%s";
    private static final String GC_URL = "http://192.168.1.103/api/v2.0/system/gc/schedule";


    public static void main(String[] args) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        // 每天定时清理
        executorService.scheduleWithFixedDelay(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                log.info("Garbage Collection Start");
                for (String project : PROJECTS) {
                    final int repoCount = getRepoCount(project);
                    final List<Repository> repositories = getRepositories(project, repoCount);
                    for (Repository repository : repositories) {
                        String repoName = repository.name.split("/")[1];
                        final List<String> artifacts = getArtifacts(project, repoName, repository.artifact_count);
                        // 只保留最近10次构建镜像
                        final int size = artifacts.size();
                        if (size > DEFAULT_REMAIN_ARTIFACT_COUNT) {
                            log.info("Project [{}] Repo [{}] Need GC", project, repoName);
                            for (int i = DEFAULT_REMAIN_ARTIFACT_COUNT; i < size; i++) {
                                deleteSpecificArtifact(project, repoName, artifacts.get(i));
                                log.info("Project [{}] Repo [{}] Artifact [{}] Delete Success", project, repoName, artifacts.get(i));
                            }
                        }
                    }
                }
                // 最后执行GC释放空间
                garbageCollect();
                log.info("Garbage Collection End");
            }
        }, 0, 1, TimeUnit.DAYS);
    }

    // 根据项目名称获取仓库数量
    private static int getRepoCount(String project) throws IOException {
        String url = String.format(GET_PROJECTS_INFO_URL, project);
        final String result = httpRequest(url, HttpMethod.GET, null);
        return JsonParser.parseString(result).getAsJsonObject().get("repo_count").getAsInt();
    }

    // 获取指定项目下所有的仓库列表
    private static List<Repository> getRepositories(String project, int size) throws IOException {
        String url = String.format(GET_REPOSITORIES_URL, project, size);
        final String result = httpRequest(url, HttpMethod.GET, null);
        return new Gson().fromJson(result, new TypeToken<List<Repository>>() {}.getType());
    }

    // 获取指定镜像仓库中的镜像列表 按照镜像push的时间排序
    private static List<String> getArtifacts(String project, String repository, int size) throws IOException {
        String url = String.format(GET_ARTIFACTS_URL, project, repository, size);
        final String result = httpRequest(url, HttpMethod.GET, null);
        List<String> artifactList = new ArrayList<>();
        JsonParser.parseString(result).getAsJsonArray().forEach(item -> artifactList.add(item.getAsJsonObject().get("digest").getAsString()));
        return artifactList;
    }

    // 根据ArtifactId删除镜像
    private static void deleteSpecificArtifact(String project, String repository, String artifactId) throws IOException {
        String url = String.format(DELETE_ARTIFACTS_URL, project, repository, artifactId);
        httpRequest(url, HttpMethod.DELETE, null);
    }

    // 触发gc
    private static void garbageCollect() throws IOException {
        String requestParameters = "{\"parameters\":{\"delete_untagged\":false,\"dry_run\":false},\"schedule\":{\"type\":\"Manual\"}}";
        httpRequest(GC_URL, HttpMethod.POST, requestParameters);
    }

    private static String httpRequest(String url, HttpMethod method, String requestBody) throws IOException {
        HttpRequestBase request = getRequest(url, method);
        request.addHeader("authorization", getAuthorization());
        if (StringUtils.isNotEmpty(requestBody)) {
            StringEntity stringEntity = new StringEntity(requestBody, "UTF-8");
            stringEntity.setContentType("application/json;charset=UTF-8");
            ((HttpEntityEnclosingRequestBase) request).setEntity(stringEntity);
        }
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
                    return EntityUtils.toString(response.getEntity(), "UTF-8");
                }
                log.error("Http Request error, HttpStatus is [{}]", statusCode);
            }
        }
        return "";
    }

    private static String getAuthorization() {
        byte[] encodeAuth = Base64.getEncoder().encode((USERNAME + ":" + PASSWORD).getBytes());
        return "Basic " + new String(encodeAuth);
    }

    private static HttpRequestBase getRequest(String url, HttpMethod method) {
        switch (method) {
            case GET:
                return new HttpGet(url);
            case POST:
                return new HttpPost(url);
            case DELETE:
                return new HttpDelete(url);
            default:
                throw new IllegalArgumentException("Unsupported method");
        }
    }

    // 仓库信息
    static class Repository {
        int artifact_count;
        String creation_time;
        int id;
        String name;
        int project_id;
        int pull_count;
        String update_time;
    }

    enum HttpMethod {
        GET, POST, DELETE
    }
}
