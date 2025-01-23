package com.example.csvtosqlbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CsvToSqlBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(CsvToSqlBatchApplication.class, args);
	}

}
