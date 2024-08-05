package ms.conta.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "queryEntityManagerFactory",
    transactionManagerRef = "queryTransactionManager",
    basePackages = {
        "ms.conta.repository.queryrepository"
    }
)
public class QueryConfig {

    @Bean(name = "queryDataSource")
    public DataSource queryDataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:postgresql://localhost:5432/query")
                .username("postgres")
                .password("postgres")
                .driverClassName("org.postgresql.Driver")
                .build();
    }

    @Bean(name = "queryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean queryEntityManagerFactory(
        EntityManagerFactoryBuilder builder,
        @Qualifier("queryDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("ms.conta.models")
                .persistenceUnit("query")
                .build();
    }

    @Bean(name = "queryTransactionManager")
    public PlatformTransactionManager queryTransactionManager(
        @Qualifier("queryEntityManagerFactory") EntityManagerFactory queryEntityManagerFactory) {
        return new JpaTransactionManager(queryEntityManagerFactory);
    }
}
