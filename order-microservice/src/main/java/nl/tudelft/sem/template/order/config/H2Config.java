package nl.tudelft.sem.template.order.config;

import javax.sql.DataSource;
import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//jackson populator imports

//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean;

/**
 * The H2 config.
 */
@Configuration
@EnableJpaRepositories("nl.tudelft.sem.template.order.domain")
@PropertySource("classpath:application-dev.properties")
@EnableTransactionManagement
public class H2Config {

    @Getter
    private final Environment environment;

    public H2Config(Environment environment) {
        this.environment = environment;
    }

    /**
     * Set up the connection to the database.
     *
     * @return The data source.
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getProperty("jdbc.driverClassName"));
        dataSource.setUrl(environment.getProperty("jdbc.url"));
        dataSource.setUsername(environment.getProperty("jdbc.user"));
        dataSource.setPassword(environment.getProperty("jdbc.pass"));

        return dataSource;
    }

    /**
     * Jackson Populator - populates the toppings table and then the products table
     * before runtime with the contents of toppings.json and products.json respectively
     *
     * Note: only uncomment this (and imports) when first run, and then comment after first run
     * i.e. only should populate the database one time - otherwise will error
     */
//    @Bean
//    public Jackson2RepositoryPopulatorFactoryBean getRespositoryPopulator() {
//        Jackson2RepositoryPopulatorFactoryBean factory = new Jackson2RepositoryPopulatorFactoryBean();
//        factory.setResources(new Resource[]{new ClassPathResource("stores.json")});
//        return factory;
//    }
}
