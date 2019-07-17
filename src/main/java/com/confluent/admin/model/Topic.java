package com.confluent.admin.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Map;

@Data
public class Topic {
    @Getter @Setter private String name;
    @Getter @Setter private int numPartitions;
    @Getter @Setter private short replicationFactor;
    @Getter @Setter private Map<String, String> configs;
    Topic() {}

    Topic(String name, int numPartitions, short replicationFactor, Map<String, String> configs) {
        this.name =name;
        this.numPartitions = numPartitions;
        this.replicationFactor = replicationFactor;
        this.configs = configs;
    }

}
