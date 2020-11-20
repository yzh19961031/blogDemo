package com.yzh.jenkins;

import com.alibaba.fastjson.JSON;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.yzh.jenkins.JenkinsConstants.*;

/**
 * 记录了项目中用到的一些JenkinsAPI
 *
 * @author yuanzhihao
 * @since 2020/11/15
 */
@Slf4j
public class JenkinsTest {

    private JenkinsServer server;

    @Before
    public void testBefore() throws URISyntaxException {
        this.server = new JenkinsServer(new URI(JENKINS_URL), JENKINS_USERNAME, JENKINS_PASSWORD);
    }

    @After
    public void testAfter() {
        if (server != null) {
            server.close();
        }
    }

    // 测试获取所有的Job任务
    @Test
    public void testGetAllJobs() throws IOException {
        Map<String, Job> jobs = server.getJobs();
        for (Map.Entry<String,Job> job:jobs.entrySet()) {
            log.info("jobName is {}",job.getKey());
        }
    }

    // 获取Job的一些属性信息
    @Test
    public void testGetJob() throws IOException {
        String jobXml = server.getJobXml(JENKINS_PROJECT_NAME);
        // 获取job的xml信息
        log.info("jenkins job xml is {}", jobXml);
    }

    // 创建Jenkins Job任务
    @Test
    public void testCreateJob() throws IOException {
        // 这边要注意一下，需要设置crumbFlag为true，这样会添加一个crumb头
        // 之前的大家的做法也都是去关闭csrf防护，但是如果为了安全，还是建议打开，只需要每次调用接口的时候添加下crumb头就可以了
        server.createJob(JENKINS_PROJECT_NAME,JENKINS_PROJECT_XML,true);
    }

    // 删除任务
    @Test
    public void testDeleteJob() throws IOException {
        server.deleteJob(JENKINS_PROJECT_NAME, true);
    }

    // 修改任务
    @Test
    public void testUpdateJob() throws IOException {
        // 这边测试用  原地更新
        server.updateJob(JENKINS_PROJECT_NAME,JENKINS_PROJECT_XML,true);
    }


    // 测试终止任务
    @Test
    public void testAbortedJob() throws IOException {
        // 这边测试终止 我在shell的构建里面添加了延时
        Map<String,String> param = new HashMap<>();
        param.put("name","yuan");
        Long buildNumber = buildJob(JENKINS_PROJECT_NAME, param);
        JobWithDetails job = server.getJob(JENKINS_PROJECT_NAME);
        Build build = job.getBuildByNumber(buildNumber.intValue());
        // 停止任务  这边停止任务没有返回值 但是可以获取到当前任务的构建状态
        build.Stop(true);
        BuildResult result = build.details().getResult();
        log.info("stop job, job status is {}", result.toString());
    }

    // 获取构建的一些信息
    @Test
    public void testGetBuildingInformation() throws IOException {
        JobWithDetails job = server.getJob(JENKINS_PROJECT_NAME);
        // 获取最后一次构建的输出
        Build lastBuild = job.getLastBuild();
        BuildWithDetails details = lastBuild.details();
        // 控制台日志
        String consoleOutputText = details.getConsoleOutputText();
        log.info("this time consoleOutputText is {}",consoleOutputText);
        // 构建结果
        BuildResult result = details.getResult();
        log.info("this time build rest is {}",result.toString());
        // 本次构建的参数
        Map<String, String> parameters = details.getParameters();
        log.info("this time build parameters is {}",parameters);
    }

    /**
     * 构建任务并且返回当前构建的buildNumber
     *
     * @param jobName 任务名称
     * @param parameters 构建参数
     * @return 当前任务的buildNumber
     * @throws IOException
     */
    private Long buildJob(String jobName, Map<String,String> parameters) throws IOException {
        // 1.获取Job信息
        JobWithDetails job = server.getJob(jobName);
        // 2.使用构建参数执行本次构建
        QueueReference queueReference = job.build(parameters,true);
        QueueItem queueItem = server.getQueueItem(queueReference);
        // 3.获取构建的buildNumber
        Executable executable = queueItem.getExecutable();
        // 这边需要进行一下轮训
        while (executable == null) {
            executable = server.getQueueItem(queueReference).getExecutable();
            sleep();
            log.info("time waiting");
        }
        Long number = executable.getNumber();
        log.info("this time build number is {}", number);
        return number;
    }

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.info(" error ");
        }
    }

    // --------------------- 直接使用rest api调用 ---------------------

    /**
     * 单独提取的一个公共方法
     *
     * @param url 请求地址
     * @param httpRequest 请求方法对象
     * @return
     */
    private String customHttpMsg(String url, HttpRequest httpRequest) throws URISyntaxException, IOException {
        URI uri = new URI(url);
        HttpHost host = new HttpHost(uri.getHost(), uri.getPort());
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        // 这边需要注意一下是使用的token代替了密码  后续会整理下项目中遇到的一个问题
        credentialsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()),
                new UsernamePasswordCredentials(JENKINS_USERNAME, JENKINS_TOKEN));
        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicScheme = new BasicScheme();
        authCache.put(host,basicScheme);
        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build()) {
            HttpClientContext httpClientContext = HttpClientContext.create();
            httpClientContext.setAuthCache(authCache);
            CloseableHttpResponse response = httpClient.execute(host, httpRequest, httpClientContext);
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        }
    }

    // 获取Job信息
    @Test
    public void testGetJob1() throws IOException, URISyntaxException {
        String url = JENKINS_URL+"/job/"+JENKINS_PROJECT_NAME+"/config.xml";
        HttpGet httpGet = new HttpGet(url);
        String res = customHttpMsg(url, httpGet);
        log.info("res is {}", res);
    }

    // 删除任务
    @Test
    public void testDeleteJob1() throws IOException, URISyntaxException {
        String url = JENKINS_URL+"/job/"+JENKINS_PROJECT_NAME+"/doDelete/api/json";
        HttpPost httpPost = new HttpPost(url);
        String res = customHttpMsg(url, httpPost);
        log.info("res is {}", res);
    }

    // 创建任务
    @Test
    public void testCreateJob1() throws IOException, URISyntaxException {
        // http://192.168.1.107:8081/jenkins/createItem/api/json?name=test
        String url = JENKINS_URL+"/createItem/api/json?name="+JENKINS_PROJECT_NAME;
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(JENKINS_PROJECT_XML, ContentType.create("text/xml", "utf-8")));
        String res = customHttpMsg(url, httpPost);
        log.info("res is {}", res);
    }


    // --------------------- No valid crumb was included in the request问题 ---------------------

    // crumb头实体对象
    @Data
    private static class CrumbEntity implements Serializable {
        private String _class;
        private String crumb;
        private String crumbRequestField;
    }

    // 获取crumb头
    private CrumbEntity getCrumb() throws IOException, URISyntaxException {
        String url = JENKINS_URL + "/crumbIssuer/api/json";
        HttpGet httpGet = new HttpGet(url);
        String res = customHttpMsg(url, httpGet);
        return JSON.parseObject(res, CrumbEntity.class);
    }

    // 测试获取crumb头
    @Test
    public void testGetCrumb() throws IOException, URISyntaxException {
        CrumbEntity crumb = getCrumb();
        log.info("crumb is {}", crumb);
    }

    // 测试方法调用前添加crumb头
    // 但是测试无效
    @Test
    public void testAddCrumb() throws IOException, URISyntaxException {
        String url = JENKINS_URL+"/createItem/api/json?name="+JENKINS_PROJECT_NAME;
        HttpPost httpPost = new HttpPost(url);
        CrumbEntity crumb = getCrumb();
        // 添加crumb头
        httpPost.addHeader(new BasicHeader(crumb.getCrumbRequestField(), crumb.getCrumb()));
        httpPost.setEntity(new StringEntity(JENKINS_PROJECT_XML, ContentType.create("text/xml", "utf-8")));
        String res = customHttpMsg(url, httpPost);
        log.info("res is {}", res);
    }

    // 使用token 调用OK
    @Test
    public void testAddWithToken() throws IOException, URISyntaxException {
        // http://192.168.1.107:8081/jenkins/createItem/api/json?name=test
        String url = JENKINS_URL+"/createItem/api/json?name="+JENKINS_PROJECT_NAME;
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(JENKINS_PROJECT_XML, ContentType.create("text/xml", "utf-8")));
        String res = customHttpMsg(url, httpPost);
        log.info("res is {}", res);
    }
}
