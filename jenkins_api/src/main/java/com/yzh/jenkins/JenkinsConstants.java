package com.yzh.jenkins;

/**
 * Jenkins一些常量信息
 *
 * @author yuanzhihao
 * @since 2020/11/16
 */
public interface JenkinsConstants {
    // jenkins的地址，主要要和jenkins上面配置的location一致
    String JENKINS_URL = "http://192.168.1.107:8081/jenkins";

    // jenkins访问的用户名
    String JENKINS_USERNAME = "yuan";

    // jenkins访问的密码
    String JENKINS_PASSWORD = "yuan1996";

    // 推荐使用token  Jenkins高版本页面上没有关闭CSRF的入口了 这个后续会整理一篇博客
    String JENKINS_TOKEN = "11b70b55236e6cd7ac9bc9ba035fedcd76";

    // 测试使用项目名称
    String JENKINS_PROJECT_NAME = "test";

    // 测试使用XML
    String JENKINS_PROJECT_XML = "<project>\n" +
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

}
