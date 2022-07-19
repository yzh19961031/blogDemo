package com.yzh;

import hudson.model.Action;
import lombok.Getter;

/**
 * 拓展插件
 *
 * @author yuanzhihao
 * @since 2022/7/19
 */
public class HelloWorldAction implements Action {
    @Getter
    private final String name;

    public HelloWorldAction(String name) {
        this.name = name;
    }

    @Override
    public String getIconFileName() {
        return "document.png";
    }

    @Override
    public String getDisplayName() {
        return "HelloWorld";
    }

    @Override
    public String getUrlName() {
        return "hello";
    }
}
