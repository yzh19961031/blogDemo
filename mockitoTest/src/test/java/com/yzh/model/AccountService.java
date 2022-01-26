package com.yzh.model;

import lombok.Setter;

/**
 * @author yuanzhihao
 * @since 2022/1/27
 */
@Setter
public class AccountService {
    // 依赖accountDao以及personDao对象
    private AccountDao accountDao;
    private PersonDao personDao;

    public double getBalance() {
        return accountDao.getBalance();
    }

    public String getName() {
        return personDao.getName();
    }
}
