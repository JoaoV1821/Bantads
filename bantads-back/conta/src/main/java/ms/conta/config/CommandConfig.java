package ms.conta.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "commandEntityManagerFactory",
    transactionManagerRef = "commandTransactionManager",
    basePackages = {
        "ms.conta.repository.commandrepository"
    }
)
public class CommandConfig {

    @Primary
    @Bean(name = "commandDataSource")
    public DataSource commandDataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:h2:mem:command;DB_CLOSE_DELAY=-1;")
                .username("sa")
                .password("")
                .driverClassName("org.h2.Driver")
                .build();
    }

    @Primary
    @Bean(name = "commandEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean commandEntityManagerFactory(
        EntityManagerFactoryBuilder builder,
        @Qualifier("commandDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("ms.conta.models")
                .persistenceUnit("command")
                .build();
    }

    @Primary
    @Bean(name = "commandTransactionManager")
    public PlatformTransactionManager commandTransactionManager(
        @Qualifier("commandEntityManagerFactory") EntityManagerFactory commandEntityManagerFactory) {
        return new JpaTransactionManager(commandEntityManagerFactory);
    }
}
