package com.example.csvtosqlbatch.config;

import com.example.csvtosqlbatch.model.Contact;
import com.example.csvtosqlbatch.processor.ContactItemProcessor;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public FlatFileItemReader<Contact> reader() {
        return new FlatFileItemReaderBuilder<Contact>()
                .name("contactItemReader")
                .resource(new ClassPathResource("contacts.csv"))
                .delimited()
                .names("id", "firstName", "lastName", "email")
                .linesToSkip(1)
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Contact.class);
                }})
                .build();
    }

    @Bean
    public ContactItemProcessor processor() {
        return new ContactItemProcessor();
    }

    @Bean
    public JpaItemWriter<Contact> writer(EntityManagerFactory entityManagerFactory) {
        JpaItemWriter<Contact> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    @Bean
    public Job importContactJob(Step step1) {
        return new JobBuilder("importContactJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(JpaItemWriter<Contact> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Contact, Contact>chunk(10, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }
}