package com.my.shardingjdbc.base;

import io.shardingjdbc.core.api.ShardingDataSourceFactory;
import io.shardingjdbc.core.api.config.ShardingRuleConfiguration;
import io.shardingjdbc.core.api.config.TableRuleConfiguration;
import io.shardingjdbc.core.api.config.strategy.InlineShardingStrategyConfiguration;
import org.apache.commons.dbcp.BasicDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class JavaConfigSample {

    public static void main(String[] args) throws SQLException {
        Map<String, DataSource> dataSourceMap = createDataSourceMap();

        // 配置Order表规则
        TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration();
        orderTableRuleConfig.setLogicTable("table_sharding");
        orderTableRuleConfig.setActualDataNodes("large${0..4}.table_sharding");

        // 配置分库 + 分表策略
        orderTableRuleConfig.setDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("id", "large${id % 5}"));
        orderTableRuleConfig.setTableShardingStrategyConfig(new InlineShardingStrategyConfiguration("id", "table_sharding"));

        // 配置分片规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(orderTableRuleConfig);

        // 获取数据源对象
        DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, new ConcurrentHashMap(), new Properties());

        DBUtil.queryTest(dataSource);

    }

    private static Map<String,DataSource> createDataSourceMap() {
        Map<String, DataSource> dataSourceMap = new HashMap<>(5);
        for (int i = 0; i <= 4; i++) {
            String dsName = "large" + i;
            dataSourceMap.put(dsName, createDataSource(dsName));
        }
        return dataSourceMap;
    }

    private static DataSource createDataSource(String dsName) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/" + dsName + "?useSSL=true");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        return dataSource;
    }

}
