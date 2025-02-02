package com.synoriq.synofin.collection.collectionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.synoriq.synofin.collection.collectionservice.common")
@ComponentScan("com.synoriq.synofin.collection.collectionservice.config")
@ComponentScan("com.synoriq.synofin.collection.collectionservice.controller")
@ComponentScan("com.synoriq.synofin.collection.collectionservice.entity")
@ComponentScan("com.synoriq.synofin.collection.collectionservice.flyway")
@ComponentScan("com.synoriq.synofin.collection.collectionservice.repository")
@ComponentScan("com.synoriq.synofin.collection.collectionservice.service")
@ComponentScan("com.synoriq.synofin.events.*")
@ComponentScan("com.synoriq.synofin.dataencryptionservice")
//@ComponentScan("com.synoriq.synofin.apipermissionvalidator")
@ComponentScan("com.synoriq.synofin.performancemonitoringservice")
public class CollectionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CollectionServiceApplication.class, args);
	}

}
