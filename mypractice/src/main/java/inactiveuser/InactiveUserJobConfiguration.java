package inactiveuser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableBatchProcessing
public class InactiveUserJobConfiguration {
    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public DataSource dataSource;

    private static final Logger log = LoggerFactory.getLogger(InactiveUserJobConfiguration.class);

    @Bean
    public ConversionService dateConversionService() {
        DefaultConversionService testConversionService = new DefaultConversionService();
        DefaultConversionService.addDefaultConverters(testConversionService);
        testConversionService.addConverter(new Converter<String, LocalDateTime>() {
            @Override
            public LocalDateTime convert(String text) {
                return LocalDateTime.parse(text, DateTimeFormatter.ISO_DATE_TIME);
            }
        });

        return testConversionService;
    }

    @Bean
    public Job userJob(JobBuilderFactory jobBuilderFactory) {
        return jobBuilderFactory.get("userJob")
                .preventRestart()
                .start(dataSettingStep())
                .next(inactiveUserStep())
                .build();
    }

    @Bean
    public Step dataSettingStep() {
        return stepBuilderFactory.get("dataSettingStep")
                .<FileUserDto, User>chunk(10)
                .reader(userFlatFileItemReader())
                .processor(fileToUserProcessor())
                .writer(userJdbcBatchItemWriter(dataSource))
                .build();

    }

    @Bean
    public FlatFileItemReader<FileUserDto> userFlatFileItemReader() {
        return new FlatFileItemReaderBuilder<FileUserDto>()
                .name("userItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names(new String[]{"id", "name", "status", "updatedTime"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<FileUserDto>() {{
                    setConversionService(dateConversionService());
                    setTargetType(FileUserDto.class);
                }})
                .build();
    }

    @Bean
    public UserItemProcessor fileToUserProcessor() {
        return new UserItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<User> userJdbcBatchItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<User>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO user (id, name, status, updated_time) VALUES (:id, :name, :status, :updatedTime)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Step inactiveUserStep() {
        return stepBuilderFactory.get("inactiveUserStep")
                .<User, User>chunk(10)
                .reader(inactiveUserReader())
                .processor(inactiveUserProcessor())
                .writer(inactiveUserWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter<User> inactiveUserWriter() {
        return ((List<? extends User> users) -> userRepository.saveAll(users));
    }

    @Bean
    @StepScope
    public ItemProcessor<User, User> inactiveUserProcessor() {
        return user -> user.toInactive();
    }

    @Bean
    @StepScope
    public QueueItemReader<User> inactiveUserReader() {
        List<User> oldUsers = userRepository.findByUpdatedDateBeforeAndStatusEquals(
                LocalDateTime.now().minusYears(1),
                UserStatus.ACTIVE.name());

        log.info("old User Count: " + oldUsers.size());
        return new QueueItemReader<>(oldUsers);
    }
}
