package com.my.shardingjdbc;

import com.my.shardingjdbc.base.DBUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.sql.DataSource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShardingjdbcApplicationTests {

    @Resource
    DataSource dataSource;

    @Test
    public void shardingQueryTest() {
        //DBUtil.queryTest(dataSource);
        DBUtil.singleQueryTest(dataSource);
    }
}

