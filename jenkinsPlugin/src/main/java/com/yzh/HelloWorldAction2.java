package com.yzh;

import hudson.model.Run;
import jenkins.model.RunAction2;
import lombok.Getter;

/**
 * 拓展插件2 支持侧面板视图回显
 *
 * @author yuanzhihao
 * @since 2022/7/19
 */
public class HelloWorldAction2 implements RunAction2 {
    @Getter
    private final String name;

    public HelloWorldAction2(String name) {
        this.name = name;
    }

    private transient Run run;

    @Override
    public void onAttached(Run<?, ?> run) {
        this.run = run;
    }

    @Override
    public void onLoad(Run<?, ?> run) {
        this.run = run;
    }

    public Run getRun() {
        return run;
    }

    @Override
    public String getIconFileName() {
        return "document.png";
    }

    @Override
    public String getDisplayName() {
        return "HelloWorld2";
    }

    @Override
    public String getUrlName() {
        return "hello2";
    }
}
