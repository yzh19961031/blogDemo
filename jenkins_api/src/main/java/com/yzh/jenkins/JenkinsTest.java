package com.yzh.jenkins;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * 记录了项目中用到的一些JenkinsAPI
 *
 * @author yuanzhihao
 * @since 2020/11/15
 */
@Slf4j
public class JenkinsTest {

    // jenkins的地址，主要要和jenkins上面配置的location一致
    private static final String JENKINS_URL = "http://192.168.1.107:8081/jenkins";

    // jenkins访问的用户名
    private static final String JENKINS_USERNAME = "yuan";

    // jenkins访问的密码
    private static final String JENKINS_PASSWORD = "yuan1996";

    // 推荐使用token  Jenkins高版本页面上没有关闭CSRF的入口了 这个后续会整理一篇博客
    private static final String JENKINS_TOKEN = "";

    // 测试使用项目名称
    private static final String JENKINS_PROJECT_NAME = "test";

    // 测试使用XML
    private static final String JENKINS_PROJECT_XML = "<project>\n" +
            "  <actions/>\n" +
            "  <description></description>\n" +
            "  <keepDependencies>false</keepDependencies>\n" +
            "  <properties>\n" +
            "    <hudson.model.ParametersDefinitionProperty>\n" +
            "      <parameterDefinitions>\n" +
            "        <hudson.model.StringParameterDefinition>\n" +
            "          <name>name</name>\n" +
            "          <description></description>\n" +
            "          <defaultValue>haha</defaultValue>\n" +
            "          <trim>false</trim>\n" +
            "        </hudson.model.StringParameterDefinition>\n" +
            "      </parameterDefinitions>\n" +
            "    </hudson.model.ParametersDefinitionProperty>\n" +
            "  </properties>\n" +
            "  <scm class=\"hudson.scm.NullSCM\"/>\n" +
            "  <canRoam>true</canRoam>\n" +
            "  <disabled>false</disabled>\n" +
            "  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>\n" +
            "  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>\n" +
            "  <triggers/>\n" +
            "  <concurrentBuild>false</concurrentBuild>\n" +
            "  <builders>\n" +
            "    <hudson.tasks.Shell>\n" +
            "      <command>echo $name</command>\n" +
            "      <configuredLocalRules/>\n" +
            "    </hudson.tasks.Shell>\n" +
            "  </builders>\n" +
            "  <publishers/>\n" +
            "  <buildWrappers/>\n" +
            "</project>";

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

}
