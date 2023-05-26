package com.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
@SpringBootApplication
@ComponentScan(basePackages = {"com.common", "com.chat"})
@ConfigurationPropertiesScan
public class MessageApplication {
        public static void main(String[] args) {
            SpringApplication.run(MessageApplication.class, args);
        }
}