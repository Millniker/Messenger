package com.friends;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = {"com.common", "com.friends"})
@ConfigurationPropertiesScan
public class FriendsApplication {
    public static void main(String[] args) {
        SpringApplication.run(FriendsApplication.class, args);
    }
}