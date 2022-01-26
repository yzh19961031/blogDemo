package com.yzh.test;

import lombok.Data;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author yuanzhihao
 * @since 2022/1/27
 */
public class MockitoVerifyTest {
    @Mock
    private Dog dog;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testVerify() {
        dog.setAge(22);
        dog.setName("Test1");
        dog.setName("Test2");

        // 验证是否设置了age为22
        Mockito.verify(dog).setAge(ArgumentMatchers.eq(22));
        // 验证是否调用了2次setName("Test1")方法
        Mockito.verify(dog, Mockito.times(1)).setName("Test1");

        // 验证是否重来没有调用过指定的方法
        Mockito.verify(dog, Mockito.never()).getAge();
        Mockito.verify(dog, Mockito.never()).setName("Test3");
        // 验证最后一次的mock方法是否是setName("Test2")
        Mockito.verify(dog, Mockito.atLeast(1)).setName("Test2");
    }

    @Data
    static class Dog {
        private int age;
        private String name;
    }
}
