package com.confluent.admin.configuration;

import com.confluent.admin.model.Acl;
import com.confluent.admin.model.Topic;
import com.confluent.admin.service.ACLService;
import com.confluent.admin.service.TopicService;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.admin.TopicListing;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;


@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("kafkaenv")
@Data
public class KafkaEnvConfiguration implements InitializingBean {
    @Setter @Getter private List<Topic>  topics;
    @Setter @Getter private List<Acl>  acls;

    @Autowired
    TopicService topicService;

    @Autowired
    ACLService aclService;


    @Value("${delete.enable}")
    private String deleteString;


    @Value("${perpetual}")
    private String perpetualString;

    @Value("${base.prefix}")
    private String basePrefix;

    @Override
    public void afterPropertiesSet() throws Exception {
        //Collection<TopicListing> topicsFromKafka = topicService.getTopics();
        //Set<String> topicNamesFromKafka= topicService.getTopicNames();
        topicService.manageTopic(topics.stream().filter(topic -> StringUtils.isEmpty(basePrefix)?true:topic.getName().startsWith(basePrefix)).collect(Collectors.toList()), new Boolean(deleteString), basePrefix);
        aclService.manageACL(acls);
        if(!new Boolean(perpetualString))
            System.exit(0);
    }

}
