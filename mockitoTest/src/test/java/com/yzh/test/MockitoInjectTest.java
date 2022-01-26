package com.yzh.test;

import com.yzh.model.AccountDao;
import com.yzh.model.AccountService;
import com.yzh.model.PersonDao;
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
public class MockitoInjectTest {
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    private AccountDao accountDao;

    @Mock
    private PersonDao personDao;

    @InjectMocks
    private AccountService accountService;

    @Test
    public void testInjectMocks() {
        // mock出accountDao以及personDao对象
        Mockito.when(accountDao.getBalance()).thenReturn(55555.0);
        Mockito.when(personDao.getName()).thenReturn("Jack");

        Assert.assertEquals(accountService.getBalance(), 55555.0, 0);
        Assert.assertEquals(accountService.getName(), "Jack");
    }
}
