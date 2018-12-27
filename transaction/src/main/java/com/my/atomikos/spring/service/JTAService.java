package com.my.atomikos.spring.service;

import com.my.atomikos.spring.mapper.db_account.AccountMapper;
import com.my.atomikos.spring.mapper.db_user.UserMapper;
import com.my.atomikos.spring.model.Account;
import com.my.atomikos.spring.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


public class JTAService {

    @Autowired
    private UserMapper userMapper; //操作db_user库
    @Autowired
    private AccountMapper accountMapper; //操作db_account库

    @Transactional
    public void insert() {
        User user = new User();
        user.setName("neil_spring1");
        userMapper.insert(user);

//        int i = 1 / 0;//模拟异常，spring回滚后，db_user库中user表中也不会插入记录

        Account account = new Account();
        account.setUserId(user.getId());
        account.setMoney(111112);
        accountMapper.insert(account);
    }

}
