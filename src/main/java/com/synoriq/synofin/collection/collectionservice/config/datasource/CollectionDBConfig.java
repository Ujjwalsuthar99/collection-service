package com.synoriq.synofin.collection.collectionservice.config.datasource;

import com.synoriq.synofin.collection.collectionservice.constant.DBConfigConstant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = DBConfigConstant.DB_COLLECTION + DBConfigConstant.ENTITY_MANAGER_FACTORY_REF,
        transactionManagerRef = DBConfigConstant.DB_COLLECTION + DBConfigConstant.TRANSACTION_MANAGER_REF,
        basePackages = {"com.synoriq.synofin.collection.collectionservice.*"})
public class CollectionDBConfig {

    @Resource
    public Environment env;


    @Bean(name = DBConfigConstant.DB_COLLECTION + DBConfigConstant.DATASOURCE)
    public DataSource dataSource() {
        CustomDBRouting collectionDataSource = new CustomDBRouting();
        collectionDataSource.setTargetDataSources(new DBInitialization(env).getDataSourceHashMap(DBConfigConstant.DB_COLLECTION));
        return collectionDataSource;
    }

    @Bean(name = DBConfigConstant.DB_COLLECTION + DBConfigConstant.ENTITY_MANAGER_FACTORY_REF)
    public LocalContainerEntityManagerFactoryBean customEntityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier(DBConfigConstant.DB_COLLECTION + DBConfigConstant.DATASOURCE) DataSource dataSource) {
        return builder.dataSource(dataSource).
        packages("com.synoriq.synofin.collection.collectionservice.*")
                .build();
    }

    @Bean(name = DBConfigConstant.DB_COLLECTION + DBConfigConstant.TRANSACTION_MANAGER_REF)
    public PlatformTransactionManager collectionTransactionManager(@Qualifier(DBConfigConstant.DB_COLLECTION + DBConfigConstant.ENTITY_MANAGER_FACTORY_REF) EntityManagerFactory customEntityManagerFactory) {
        return new JpaTransactionManager(customEntityManagerFactory);
    }
}
