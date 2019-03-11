package com.itheima.dao.impl;

import com.itheima.dao.IAccountDao;
import com.itheima.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AccountDaoImpl implements IAccountDao {

    // 注入jdbcTemplate
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void save(Account account) {
        jdbcTemplate.update("insert into account values(null,?,?)",
                account.getUid(),account.getMoney());
    }
}












