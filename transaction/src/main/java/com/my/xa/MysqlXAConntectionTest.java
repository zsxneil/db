package com.my.xa;

import com.mysql.jdbc.jdbc2.optional.MysqlXAConnection;
import com.mysql.jdbc.jdbc2.optional.MysqlXid;

import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * MySQL Connector/J 从5.0.0版本之后开始直接提供对XA的支持，也就是提供了java版本XA接口的实现。意味着我们可以直接通过java代码来执行mysql xa事务。

 需要注意的是，业务开发人员在编写代码时，不应该直接操作这些XA事务操作的接口。因为在DTP模型中，RM上的事务分支的开启、结束、准备、提交、回滚等操作，都应该是由事务管理器TM来统一管理。

 由于目前我们还没有接触到TM，那么我们不妨做一回"人肉事务管理器"，用你智慧的大脑，来控制多个mysql实例上xa事务分支的执行，提交/回滚。通过直接操作这些接口，你将对xa事务有更深刻的认识。
 */
public class MysqlXAConntectionTest {

    public static void main(String[] args) throws SQLException {
        //true表示打印出执行语句，便于调试
        boolean logXaCommands = true;
        //获得资源管理器操作接口实例 RM1
        Connection conn1 = DriverManager.getConnection("jdbc:mysql://localhost:3306/common?useSSL=true", "root" ,"123456");
        XAConnection xaConn1 = new MysqlXAConnection((com.mysql.jdbc.Connection) conn1, logXaCommands);
        XAResource rm1 = xaConn1.getXAResource();
        //获得资源管理器操作接口实例 RM2
        Connection conn2 = DriverManager.getConnection("jdbc:mysql://localhost:3306/common?useSSL=true", "root" ,"123456");
        XAConnection xaConn2 = new MysqlXAConnection((com.mysql.jdbc.Connection) conn2, logXaCommands);
        XAResource rm2 = xaConn2.getXAResource();

        //AP请求TM执行一个分布式事务，TM生成全局事务id
        byte[] gtrid = "g12345".getBytes();
        int formatId = 1;
        try {

            // ==============分别执行RM1和RM2上的事务分支====================
            //TM生成rm1上的事务id
            byte[] bqual1 = "b00001".getBytes();
            Xid xid1 = new MysqlXid(gtrid, bqual1, formatId);
            //执行rm1上的事务分支
            rm1.start(xid1, XAResource.TMNOFLAGS);//One of TMNOFLAGS, TMJOIN, or TMRESUME. 源码中显示，此处定义的是join,resume,或者什么都不做
            PreparedStatement ps1 = conn1.prepareStatement("INSERT INTO user(name) VALUES ('neil2')");
            ps1.execute();
            rm1.end(xid1, XAResource.TMSUCCESS);

            //TM生成rm2上的事务id
            byte[] bqual2 = "b00002".getBytes();
            Xid xid2 = new MysqlXid(gtrid, bqual2, formatId);
            //执行rm1上的事务分支
            rm2.start(xid2, XAResource.TMNOFLAGS);
            PreparedStatement ps2 = conn2.prepareStatement("INSERT INTO user(name) VALUES ('neil3')");
            ps2.execute();
            rm2.end(xid2, XAResource.TMSUCCESS);

            // ===================两阶段提交================================
            //phase1: 询问所有的rm，准备提交事务分支
            int rm1_prepare = rm1.prepare(xid1);
            int rm2_prepare = rm2.prepare(xid2);
            //phase2: 提交所有事务分支
            boolean onePhrase = false;  //TM判断有2个事务分支，所以不能优化为一阶段提交
            if (rm1_prepare == XAResource.XA_OK && rm2_prepare == XAResource.XA_OK) {
                //所有事务分支都prepare成功，提交所有事务分支
                rm1.commit(xid1, onePhrase);
                rm2.commit(xid2, onePhrase);
            } else {//如果有事务分支没有成功，则回滚
                rm1.rollback(xid1);
                rm2.rollback(xid2);
            }
        } catch (Exception e) {
            //如果出现异常也需要回滚
            e.printStackTrace();
        }
    }

}
