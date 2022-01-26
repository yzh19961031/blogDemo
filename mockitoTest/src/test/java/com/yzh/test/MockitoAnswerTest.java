package com.yzh.test;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author yuanzhihao
 * @since 2022/1/27
 */
public class MockitoAnswerTest {
    @Test
    public void doAnswerTest() {
        final Car car = Mockito.mock(Car.class);
        Mockito.doAnswer(invocation -> {
            // 根据参数计算返回值
            final String color = invocation.getArgument(0, String.class);
            final String brand = invocation.getArgument(1, String.class);
            return color + " " + brand;
        }).when(car).getName("red", "BMW");
        Assert.assertEquals(car.getName("red", "BMW"), "red BMW");
    }

    static class Car {
        public String getName(String color, String brand) {
            return "";
        }
    }
}
