package com.liquibase.application.config;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.liquibase.application.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.core.convert.CouchbaseCustomConversions;

@Slf4j
@Configuration
public class CouchbaseConfig extends AbstractCouchbaseConfiguration {
    @Value("${spring.couchbase.connection-string}")
    private String connectionString;

    @Value("${spring.couchbase.username}")
    private String username;

    @Value("${spring.couchbase.password}")
    private String password;

    @Value("${spring.couchbase.bucket}")
    private String bucketName;

    @Value("${spring.couchmove.enabled: false}")
    private boolean couchmoveEnabled;

    @Value("${spring.couchmove.waitIndexesInSeconds: 10}")
    private long waitIndexesInSeconds;

    private final Environment env;

    @Autowired
    public CouchbaseConfig(Environment env) {
        this.env = env;
    }

    @Override
    public String getConnectionString() {
        return connectionString;
    }

    @Override
    public String getUserName() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getBucketName() {
        return bucketName;
    }


    @Bean
    public CustomConversions customConversions() {
        return new CouchbaseCustomConversions(List.of(
                StringToLocalTimeConverter.INSTANCE, LocalTimeToStringConverter.INSTANCE,
                StringToLocalDateConverter.INSTANCE, LocalDateToStringConverter.INSTANCE));
    }

    /**
     * Converter for Couchbase, which reads LocalTime values from String representation in the DB.
     */
    @ReadingConverter
    public enum StringToLocalTimeConverter implements Converter<String, LocalTime> {
        INSTANCE;

        @Override
        public LocalTime convert(String source) {
            return LocalTime.parse(source);
        }
    }

    /**
     * Converter for Couchbase, which writes LocalTime values as String representation into the DB.
     */
    @WritingConverter
    public enum LocalTimeToStringConverter implements Converter<LocalTime, String> {
        INSTANCE;

        @Override
        public String convert(LocalTime source) {
            return source.toString();
        }
    }

    /**
     * Converter for Couchbase, which reads LocalDate values from String representation in the DB.
     */
    @ReadingConverter
    public enum StringToLocalDateConverter implements Converter<String, LocalDate> {
        INSTANCE;

        @Override
        public LocalDate convert(String source) {
            return LocalDate.parse(source, DateTimeFormatter.ofPattern(DateTimeUtil.YYYY_MM_DD));
        }
    }

    /**
     * Converter for Couchbase, which writes LocalDate values as String representation into the DB.
     */
    @WritingConverter
    public enum LocalDateToStringConverter implements Converter<LocalDate, String> {
        INSTANCE;

        @Override
        public String convert(LocalDate source) {
            return source.format(DateTimeFormatter.ofPattern(DateTimeUtil.YYYY_MM_DD));
        }
    }
}
