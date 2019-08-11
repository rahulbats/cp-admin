package com.confluent.admin.configuration;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AdminClientConfiguration {
    @Value("${spring.kafka.bootstrap-servers}")
    private String brokerAsString;


    @Value("${sasl.jaas.config}")
    private String jaasConfig;

    @Value("${sasl.mechanism}")
    private String saslMechanism;

    @Value("${security.protocol}")
    private String securityProtocol;

    @Value("${ssl.truststore.location}")
    private String truststoreLocation;

    @Value("${ssl.truststore.password}")
    private String trustStorePassword;

    @Value("${ssl.keystore.location}")
    private String keyStoreLocation;

    @Value("${ssl.keystore.password}")
    private String keyStorePassword;

    @Value("${ssl.key.password}")
    private String keyPassword;

    @Bean
    public AdminClient adminClient() {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokerAsString);
        config.put("security.protocol",securityProtocol);
        config.put("sasl.jaas.config",jaasConfig);
        config.put("sasl.mechanism",saslMechanism);
        if(!StringUtils.isEmpty(truststoreLocation))
            config.put("ssl.truststore.location",truststoreLocation);
        if(!StringUtils.isEmpty(keyStoreLocation))
            config.put("ssl.keystore.location",keyStoreLocation);
        if(!StringUtils.isEmpty(keyStorePassword))
            config.put("ssl.keystore.password",keyStorePassword);
        if(!StringUtils.isEmpty(keyPassword))
            config.put("ssl.key.password",keyPassword);
        if(!StringUtils.isEmpty(trustStorePassword))
            config.put("ssl.truststore.password",trustStorePassword);

        return AdminClient.create(config);
    }
}
