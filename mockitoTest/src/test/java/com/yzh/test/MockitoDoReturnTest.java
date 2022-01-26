package com.yzh.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.LinkedList;
import java.util.List;

/**
 * @author yuanzhihao
 * @since 2022/1/27
 */
public class MockitoDoReturnTest {
    @Spy
    List<String> spyList = new LinkedList<>();

    @Mock
    List<String> mockList;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void doReturnTest() {
        // Mockito.when(spyList.get(0)).thenReturn("Hello");
        // Assert.assertEquals(spyList.get(0), "Hello");
        // 这边会抛出java.lang.IndexOutOfBoundsException: Index: 0, Size: 0 异常
        // spyList其实是一个空列表
        Mockito.doReturn("Hello").when(spyList).get(0);
        Assert.assertEquals(spyList.get(0), "Hello");

        Mockito.doReturn("World").when(mockList).get(1);
        Assert.assertEquals(mockList.get(1), "World");
    }
}
