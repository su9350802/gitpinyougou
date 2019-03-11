package com.itheima;

import com.itheima.entity.Account;
import com.itheima.service.IAccountService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

public class App {

    @Test
    public void save(){
        ApplicationContext ac =
                new ClassPathXmlApplicationContext("bean.xml");
        // 根据类型从容器获取对象，注意：该类型的对象在容器中要唯一。
        IAccountService accountService = ac.getBean(IAccountService.class);
        System.out.println("查看代理：" + accountService.getClass());
        // 执行方法
        accountService.save(new Account());
    }
}
