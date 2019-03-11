package com.itheima.service;

import com.itheima.entity.Account;
import org.springframework.transaction.annotation.Transactional;


public interface IAccountService {
    /**
     * 保存账户
     */
    void save(Account account);
}
