package com.example.csvtosqlbatch.config;

import com.example.csvtosqlbatch.model.Contact;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;

@Configuration
public class BatchConfig {

    @Bean
    public FlatFileItemReader<Contact> reader() {
        return new FlatFileItemReaderBuilder<Contact>()
                .name("contactItemReader")
                .resource(new ClassPathResource("contacts.csv"))
                .delimited()
                .names("id", "firstName", "lastName", "email")
                .linesToSkip(1)
                .targetType(Contact.class)
                .build();
    }

    @Bean
    public JpaItemWriter<Contact> writer(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<Contact>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public Job importContactJob(JobRepository jobRepository, Step step1) {
        return new JobBuilder("importContactJob", jobRepository)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                      FlatFileItemReader<Contact> reader, JpaItemWriter<Contact> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Contact, Contact>chunk(10, transactionManager)
                .reader(reader)
                .writer(writer)
                .build();
    }
}
