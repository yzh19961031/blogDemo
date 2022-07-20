package com.yzh;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import lombok.Getter;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 测试jenkins插件
 *
 * @author yuanzhihao
 * @since 2022/6/14
 */
public class MyBuilder extends Builder {
    @Getter
    private final String name;

    @DataBoundConstructor
    public MyBuilder(String name) {
        this.name = name;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        PrintStream logger = listener.getLogger();
        logger.println("Hello " + name);
        // 添加自定义action
        build.addAction(new HelloWorldAction(name));
        build.addAction(new HelloWorldAction2(name));
        // 提供对外api接口
        build.addAction(new HelloWorldAction3(name, new SimpleDateFormat("yyyy-MM-dd").format(new Date())));

        boolean printLog = getDescriptor().isPrintLog();
        if (printLog) {
            logger.println("Log is " + getDescriptor().getLog());
        }
        return true;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {
        @Getter
        private boolean printLog;
        @Getter
        private String log;

        private static final String PLUGIN_NAME = "MyPlugin";

        public DescriptorImpl() {
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        // 插件任务在jenkins上面的显示名称
        @NonNull
        @Override
        public String getDisplayName() {
            return PLUGIN_NAME;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            printLog = json.getBoolean("printLog");
            log = json.getString("log");
            // 将配置写入磁盘
            save();
            return super.configure(req, json);
        }
    }
}
