package com.liquibase.application.config;

import org.liquibase.ext.couchbase.starter.configuration.CouchbaseLiquibaseAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

@Configuration
@Import(CouchbaseLiquibaseAutoConfiguration.class)
public class LiquibaseConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(LiquibaseConfiguration.class);

    private final Environment env;

    public LiquibaseConfiguration(Environment env) {
        this.env = env;
    }

  /*@Bean
  public SpringLiquibase liquibase(
      @Qualifier("taskExecutor") Executor executor,
      @LiquibaseDataSource ObjectProvider<DataSource> liquibaseDataSource,
      LiquibaseProperties liquibaseProperties,
      ObjectProvider<DataSource> dataSource,
      DataSourceProperties dataSourceProperties,
      ApplicationProperties applicationProperties
  ) {
    SpringLiquibase liquibase;
    boolean doAsync = applicationProperties.getLiquibase().getAsync();
    LOGGER.info("Configuring Liquibase async mode: {}", doAsync);
    if (doAsync) {
      liquibase =
          SpringLiquibaseUtil.createAsyncSpringLiquibase(
              this.env,
              executor,
              liquibaseDataSource.getIfAvailable(),
              liquibaseProperties,
              dataSource.getIfUnique(),
              dataSourceProperties
          );
    } else {
      liquibase =
          SpringLiquibaseUtil.createSpringLiquibase(
              liquibaseDataSource.getIfAvailable(),
              liquibaseProperties,
              dataSource.getIfUnique(),
              dataSourceProperties
          );
    }
    liquibase.setChangeLog("classpath:config/liquibase/master.xml");
    liquibase.setContexts(liquibaseProperties.getContexts());
    liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
    liquibase.setLiquibaseSchema(liquibaseProperties.getLiquibaseSchema());
    liquibase.setLiquibaseTablespace(liquibaseProperties.getLiquibaseTablespace());
    liquibase.setDatabaseChangeLogLockTable(liquibaseProperties.getDatabaseChangeLogLockTable());
    liquibase.setDatabaseChangeLogTable(liquibaseProperties.getDatabaseChangeLogTable());
    liquibase.setDropFirst(liquibaseProperties.isDropFirst());
    liquibase.setLabels(liquibaseProperties.getLabels());
    liquibase.setChangeLogParameters(liquibaseProperties.getParameters());
    liquibase.setRollbackFile(liquibaseProperties.getRollbackFile());
    liquibase.setTestRollbackOnUpdate(liquibaseProperties.isTestRollbackOnUpdate());
    if (env.acceptsProfiles(Profiles.of(TradeSignalProfiles.SPRING_PROFILE_NO_LIQUIBASE))) {
      liquibase.setShouldRun(false);
      LOGGER.info("Skip configuring Liquibase");
    } else {
      boolean shouldRun = liquibaseProperties.isEnabled();
      LOGGER.info("Configuring Liquibase: {}", shouldRun);
      liquibase.setShouldRun(shouldRun);
    }
    return liquibase;
  }*/
}
