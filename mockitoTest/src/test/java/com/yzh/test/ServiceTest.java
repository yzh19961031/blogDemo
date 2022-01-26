package com.yzh.test;

import com.yzh.model.Database;
import com.yzh.model.Service;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author yuanzhihao
 * @since 2022/1/27
 */
public class ServiceTest {
    @Mock
    Database database; // 2

    @InjectMocks
    Service service; // 3

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this); // 1
    }

    @Test
    public void testQuery() {
        Assert.assertNotNull(database);
        Mockito.when(database.isAvailable()).thenReturn(true); // 4
        final boolean query = service.query("select * from database"); // 5
        Assert.assertTrue(query);
    }
}