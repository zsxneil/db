package com.my.insertmysql.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

@Service
public class MultiInsert {

    private static final Logger log = LoggerFactory.getLogger(MultiInsert.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * 批量插入sum条记录
     * @param sum
     */
    public void insertBatch(int sum) {

        int coreSize = Runtime.getRuntime().availableProcessors();
        log.info("coreSize:{}", coreSize);
        int recordSize = sum / coreSize;
        CountDownLatch latch = new CountDownLatch(coreSize);
        int mod = sum % coreSize;
        int start = 0;
        long startTime = System.currentTimeMillis();
        for (int i=0; i<coreSize; i++) {
            int startIndex = start;
            int endIndex = startIndex + recordSize - 1;
            if (i < mod) {
                endIndex += 1;
            }
            start = endIndex + 1;
            new MultiInsertThread(jdbcTemplate, startIndex, endIndex, latch).start();
        }
        try {
            latch.await();
            log.info("cost time:{}", (System.currentTimeMillis() - startTime));
        } catch (InterruptedException e) {
            log.error(e.getLocalizedMessage(), e);
        }

    }

}
