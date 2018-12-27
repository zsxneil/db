package com.my.insertmysql.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.concurrent.CountDownLatch;

public class MultiInsertThread extends Thread {

    private static  final Logger log = LoggerFactory.getLogger(MultiInsertThread.class);

    JdbcTemplate jdbcTemplate;
    int startIndex;
    int endIndex;
    CountDownLatch latch;

    public MultiInsertThread(JdbcTemplate jdbcTemplate, int startIndex, int endIndex, CountDownLatch latch) {
        this.jdbcTemplate = jdbcTemplate;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            log.info("start:{};end:{}", startIndex, endIndex);
            String insertSql = "INSERT INTO multi_genarate(uuid) VALUES";
            StringBuilder builder = new StringBuilder();
            int count = endIndex - startIndex + 1;
            for (int id = 1; id <= count; id++) {
                builder.append("(UUID()),");
                if (id % 10000 == 0 || id >= count) {
                    insertSql = insertSql + builder.deleteCharAt(builder.length() - 1).toString();
                    //log.info("insertSql:{};id:{}", insertSql, id);
                    try {
                        jdbcTemplate.execute(insertSql);
                    } catch (Exception e) {
                        log.error(e.getLocalizedMessage(), e);
                    }
                    builder = new StringBuilder();
                    insertSql = "INSERT INTO multi_genarate(uuid) VALUES";
                }
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
        latch.countDown();
    }
}
