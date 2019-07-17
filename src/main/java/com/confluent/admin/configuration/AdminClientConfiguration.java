package com.confluent.admin.configuration;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AdminClientConfiguration {
    @Value("${spring.kafka.bootstrap-servers}")
    private String brokerAsString;


    @Value("${sasl.jaas.config}")
    private String jaasConfig;

    @Bean
    public AdminClient adminClient() {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokerAsString);
        config.put("endpoint.identification.algorithm","HTTPS");
        config.put("security.protocol","SASL_SSL");
        config.put("sasl.jaas.config",jaasConfig);
        config.put("sasl.mechanism","PLAIN");


        return AdminClient.create(config);
    }
}
