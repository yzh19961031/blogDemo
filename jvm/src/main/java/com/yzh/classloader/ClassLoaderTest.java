package com.yzh.classloader;

import com.sun.java.accessibility.util.AWTEventMonitor;
import org.junit.Test;
import sun.misc.Launcher;

import java.net.URL;

/**
 * 类加载器测试
 *
 * @author yuanzhihao
 * @since 2021/3/8
 */
public class ClassLoaderTest {

    @Test
    public void testGetClassLoader() {
        // 获取系统类加载器
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        System.out.println(systemClassLoader);
        // 通过classloader.getParent()方法获取系统类加载器的父类加载器，拓展类加载器
        ClassLoader extClassLoader = systemClassLoader.getParent();
        System.out.println(extClassLoader);
        // 获取拓展类系统加载器，启动类加载器，注意启动类加载器输出为null
        ClassLoader bootstrapClassLoader = extClassLoader.getParent();
        System.out.println(bootstrapClassLoader);
    }

    @Test
    public void testClassLoader2() {
        // 启动类加载器 输出所有加载的jar信息
        System.out.println("--------启动类加载器-----------");
        URL[] urLs = Launcher.getBootstrapClassPath().getURLs();
        for (URL url:urLs) {
            System.out.println(url.toExternalForm());
        }

        // String类是rt.jar下面的一个类，它的classloader是引导类加载器
        ClassLoader classLoader = String.class.getClassLoader();
        System.out.println(classLoader);

        System.out.println("--------拓展类加载器-----------");
        // 扩展类加载器 输出加载的目录信息
        String property = System.getProperty("java.ext.dirs");
        String[] split = property.split(":");
        for (String s:split) {
            System.out.println(s);
        }
        // AWTEventMonitor是拓展类加载的一个类，可以看到它的类加载器是ExtClassLoader
        ClassLoader classLoader1 = AWTEventMonitor.class.getClassLoader();
        System.out.println(classLoader1);

        System.out.println("--------系统类加载器-----------");
        ClassLoader classLoader2 = ClassLoaderTest.class.getClassLoader();
        System.out.println(classLoader2);
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        // 这边获得的类加载器是一样的，都是系统类加载器
        System.out.println(systemClassLoader);
    }

}
