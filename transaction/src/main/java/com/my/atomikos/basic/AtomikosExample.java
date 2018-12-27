package com.my.atomikos.basic;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.jdbc.AtomikosDataSourceBean;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.sql.*;
import java.util.Properties;

/**
 * 在使用了事务管理器之后，我们通过atomikos提供的
 * UserTransaction接口的实现类com.atomikos.icatch.jta.UserTransactionImp来开启、提交和回滚事务。
 * 而不再是使用java.sql.Connection中的setAutoCommit(false)的方式来开启事务。
 * 其他JTA规范中定义的接口，开发人员并不需要直接使用。
 */
public class AtomikosExample {

    private static AtomikosDataSourceBean createAtomikosDataSourceBean(String dbName) {

        //连接池基本属性
        Properties properties = new Properties();
        properties.setProperty("url", "jdbc:mysql://localhost:3306/" + dbName);
        properties.setProperty("user", "root");
        properties.setProperty("password", "123456");

        //使用AtomikosDataSourceBean封装com.mysql.jdbc.jdbc2.optional.MysqlXADataSource
        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
        ds.setUniqueResourceName(dbName);
        ds.setXaProperties(properties);
        ds.setXaDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");

        return ds;
    }

    public static void main(String[] args) {
        AtomikosDataSourceBean ds1 = createAtomikosDataSourceBean("db_user");
        AtomikosDataSourceBean ds2 = createAtomikosDataSourceBean("db_account");

        Connection conn1 = null;
        Connection conn2 = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;

        UserTransaction userTransaction = new UserTransactionImp();

        try {
            //开启事务
            userTransaction.begin();

            //执行db1上的sql
            conn1 = ds1.getConnection();
            ps1 = conn1.prepareStatement("INSERT  INTO user(name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, "neil_basic1");
            ps1.executeUpdate();

            ResultSet generated_keys = ps1.getGeneratedKeys();
            int userId = -1;
            while (generated_keys.next()) {
                userId = generated_keys.getInt(1); //获取自动生成的id
            }

            //模拟异常，两个事务均不提交
           // int i = 1/0;

            //执行db2上的sql
            conn2 = ds2.getConnection();
            ps2 = conn2.prepareStatement("INSERT INTO account(user_id, money) VALUES (?, ?)");
            ps2.setInt(1, userId);
            ps2.setDouble(2, 100000);
            ps2.executeUpdate();

            //两阶段提交
            userTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                userTransaction.rollback();
            } catch (SystemException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (ps1 != null) {
                    ps1.close();
                }
                if (ps2 != null) {
                    ps2.close();
                }
                if (conn1 != null) {
                    conn1.close();
                }
                if (conn2 != null) {
                    conn2.close();
                }
                if (ds1 != null) {
                    ds1.close();
                }
                if (ds2 != null) {
                    ds2.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
