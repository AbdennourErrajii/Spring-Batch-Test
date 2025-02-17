package com.abdo.springbatchcustomer.config;

import com.abdo.springbatchcustomer.entity.Equipement;
import com.abdo.springbatchcustomer.repo.EquipementRepo;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@AllArgsConstructor
public class BatchConfigForJSON {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EquipementRepo equipementRepo;

    @Bean
    public JsonItemReader<Equipement> equipementReader() {
        return new JsonItemReaderBuilder<Equipement>()
                .name("equipementReader")
                .resource(new ClassPathResource("inputs/equipement.json"))
                .jsonObjectReader(new JacksonJsonObjectReader<>(Equipement.class))
                .build();
    }

    @Bean
    public ItemWriter<Equipement> equipementWriter() {
        return equipements -> equipementRepo.saveAll(equipements);
    }

    @Bean
    public ItemProcessor<Equipement, Equipement> equipementProcessor() {
        return equipement -> {
            equipement.setName(equipement.getName().toLowerCase());
            return equipement;
        };
    }

    @Bean
    public Step stepJsonToDB(JsonItemReader<Equipement> reader,
                             ItemWriter<Equipement> writer,
                             JobRepository jobRepository,
                             PlatformTransactionManager transactionManager) {

        var builder = new StepBuilder("stepJsonToDB", jobRepository);
        return builder
                .<Equipement, Equipement>chunk(1, transactionManager)
                .reader(reader)
                .processor(equipementProcessor())
                .writer(writer)
                .build();
    }

    @Bean
    public Job runJob3() throws Exception {
        var builder = new JobBuilder("runJob3", jobRepository);
        return builder
                .start(stepJsonToDB(equipementReader(), equipementWriter(), jobRepository, transactionManager))
                .build();
    }


}
