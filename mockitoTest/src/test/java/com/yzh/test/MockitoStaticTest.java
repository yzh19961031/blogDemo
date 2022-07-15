package com.yzh.test;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

/**
 * 模拟静态方法
 *
 * @author yuanzhihao
 * @since 2022/7/15
 */
public class MockitoStaticTest {
    @Test
    public void testStaticWithNoArgs() {
        try (final MockedStatic<StaticUtils> staticUtilsMockedStatic = Mockito.mockStatic(StaticUtils.class)) {
            staticUtilsMockedStatic.when(StaticUtils::getName).thenReturn("wu");
            Assert.assertEquals(StaticUtils.getName(), "wu");
        }
    }

    @Test
    public void testStaticWithArgs() {
        try (MockedStatic<StaticUtils> staticUtilsMockedStatic = Mockito.mockStatic(StaticUtils.class)) {
            staticUtilsMockedStatic.when(() -> StaticUtils.add(Mockito.anyInt(), Mockito.anyInt())).thenReturn(55);
            Assert.assertEquals(StaticUtils.add(1,3), 52);
        }
    }

    static class StaticUtils {
        static String getName() {
            return "yuan";
        }

        static int add(int x, int y) {
            return x + y;
        }
    }
}
