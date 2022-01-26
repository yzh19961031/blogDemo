package com.yzh.test;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyInt;

/**
 * @author yuanzhihao
 * @since 2022/1/27
 */
public class MockitoThrowTest {

    @Test
    public void testThrow() {
        // 这边使用静态的mock方法模拟对象
        Demo demo = Mockito.mock(Demo.class);
        Mockito.when(demo.getAge(anyInt())).thenThrow(new IllegalArgumentException("Warning"));
        IllegalArgumentException exception = Assert.assertThrows(IllegalArgumentException.class, () -> demo.getAge(33));
        Assert.assertEquals(exception.getMessage(), "Warning");
    }

    static class Demo {
        public int getAge(int age) {
            return age;
        }
    }
}
