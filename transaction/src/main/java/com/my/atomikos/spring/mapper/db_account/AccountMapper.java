package com.my.atomikos.spring.mapper.db_account;

import com.my.atomikos.spring.model.Account;
import org.apache.ibatis.annotations.Insert;

public interface AccountMapper {
    @Insert("insert into account(user_id, money) values(#{userId},#{money})")
    public void insert(Account account);
}
