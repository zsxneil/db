package com.my.atomikos.spring.mapper.db_user;

import com.my.atomikos.spring.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

public interface UserMapper {

    @Insert("insert into user(id,name) values(#{id},#{name})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    public void insert(User user);

}
