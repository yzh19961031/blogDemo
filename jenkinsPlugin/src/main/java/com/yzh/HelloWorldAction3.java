package com.yzh;

import hudson.model.Action;
import hudson.model.Api;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * 设置属性可以通过jenkins api访问
 *
 * @author yuanzhihao
 * @since 2022/7/20
 */
@ExportedBean
public class HelloWorldAction3 implements Action {
    private final String name;
    private final String date;

    public HelloWorldAction3(String name, String date) {
        this.name = name;
        this.date = date;
    }

    @Exported
    public String getName() {
        return name;
    }

    @Exported
    public String getDate() {
        return date;
    }

    // 表示HelloWorldAction3可以通过/api/json或者/api/xml的方式暴露出来
    public Api getApi() {
        return new Api(this);
    }

    @Override
    public String getIconFileName() {
        return "document.png";
    }

    @Override
    public String getDisplayName() {
        return "HelloWorld3";
    }

    // 配置URL地址
    @Override
    public String getUrlName() {
        return "hello3";
    }
}
