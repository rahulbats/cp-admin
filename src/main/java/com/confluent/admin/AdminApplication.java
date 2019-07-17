package com.confluent.admin;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }

}
