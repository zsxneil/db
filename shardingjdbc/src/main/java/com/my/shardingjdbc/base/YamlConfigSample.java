package com.my.shardingjdbc.base;

import io.shardingjdbc.core.api.ShardingDataSourceFactory;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

public class YamlConfigSample {

    public static void main(String[] args) throws IOException, SQLException {
        DataSource dataSource = ShardingDataSourceFactory.createDataSource(ResourceUtils.getFile("classpath:table_sharding.yml"));

//        DBUtil.queryTest(dataSource);
        DBUtil.updateTest(dataSource);
    }

}
