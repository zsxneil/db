package com.my.insertmysql;

import com.my.insertmysql.service.MultiInsert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class InsertmysqlApplication {

	public static void main(String[] args) {
		SpringApplication.run(InsertmysqlApplication.class, args);
	}

	@Autowired
	MultiInsert multiInsert;

	@Component
	@Order(1)
	class InsertRunner implements ApplicationRunner {

		@Override
		public void run(ApplicationArguments args) throws Exception {
			multiInsert.insertBatch(1000000);
		}
	}

}

