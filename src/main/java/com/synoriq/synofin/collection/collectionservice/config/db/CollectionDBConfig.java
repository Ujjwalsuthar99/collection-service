//package com.synoriq.synofin.collection.collectionservice.config.db;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//
//import javax.annotation.Resource;
//import javax.sql.DataSource;
//
//@Configuration
//public class CollectionDBConfig {
//
//    @Resource
//    public Environment env;
//
//    @Bean("DataSource")
//    public DataSource dataSource() {
//        CustomDBRouting lmsDataSource = new CustomDBRouting();
//        lmsDataSource.setTargetDataSources(new DBInitialization(env).getDataSourceHashMap());
//        return lmsDataSource;
//    }
//}