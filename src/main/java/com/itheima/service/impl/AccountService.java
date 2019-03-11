package com.itheima.service.impl;

import com.itheima.dao.IAccountDao;
import com.itheima.entity.Account;
import com.itheima.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class AccountService implements IAccountService {

    // 注入dao
    @Autowired
    private IAccountDao accountDao;
    // 注入事务操作模板
    @Autowired
    private TransactionTemplate transactionTemplate;


    @Override
    public void save(Account account) {
        // 创建回调函数，把需要进行事务控制的代码放入回调函数中。spring会自动进行事务控制
        TransactionCallback<Object> callback = new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                accountDao.save(account);
                //int i = 1/0;
                accountDao.save(account);
                return null;
            }
        };
        // 执行事务控制，传入回调函数
        transactionTemplate.execute(callback);
    }
}











