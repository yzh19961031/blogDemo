package com.yzh.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Comparator;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;

/**
 * @author yuanzhihao
 * @since 2022/1/27
 */
public class MockitoWhenTest {
    @Mock
    List<String> mockList;

    @Mock
    Comparator<Integer> comparator;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    // 测试返回指定的值 指定参数
    @Test
    public void testReturnConfiguredValue() {
        Mockito.when(mockList.get(0)).thenReturn("Hello");
        Assert.assertEquals(mockList.get(0), "Hello");
    }

    // mock可以指定多个返回值 会按照顺序返回
    @Test
    public void testMoreThanOneReturnValue() {
        Mockito.when(mockList.get(0)).thenReturn("Hello").thenReturn("World");
        Assert.assertEquals(mockList.get(0), "Hello");
        Assert.assertEquals(mockList.get(0), "World");
    }

    // 可以指定anyInt anyString等类型 不限定参数的输入 都返回配置的值
    @Test
    public void testReturnValueUseAnyParameter() {
        // 测试用 不要在意功能
        Mockito.when(comparator.compare(9999,9999)).thenReturn(100);
        Assert.assertEquals(comparator.compare(9999,9999), 100);

        Mockito.when(comparator.compare(anyInt(), anyInt())).thenReturn(222);
        Assert.assertEquals(comparator.compare(9,232), 222);
    }
}
