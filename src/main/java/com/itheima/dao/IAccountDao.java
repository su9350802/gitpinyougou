package com.itheima.dao;

import com.itheima.entity.Account;

public interface IAccountDao {
    /**
     * 保存账户
     */
    void save(Account account);
}
