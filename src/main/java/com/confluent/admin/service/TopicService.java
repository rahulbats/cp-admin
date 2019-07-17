package com.confluent.admin.service;

import com.confluent.admin.model.Topic;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TopicService {
    @Autowired
    private AdminClient adminClient;

    public Set<String> getTopicNames() throws InterruptedException, ExecutionException {
        return adminClient.listTopics().names().get();
    }

    public Collection<TopicListing> getTopics() throws InterruptedException, ExecutionException {
        return adminClient.listTopics().listings().get();
    }





    public String createTopic(@RequestBody List<Topic> topics) throws Exception{



        List<NewTopic> newTopics = topics.stream().map(topic -> {
            NewTopic newTopic = new NewTopic(topic.getName(), topic.getNumPartitions(), topic.getReplicationFactor());
            newTopic.configs(topic.getConfigs());
            return newTopic;
        }).collect(Collectors.toList());



        CreateTopicsResult result = adminClient.createTopics(newTopics);
        while (!result.all().isDone())
            Thread.sleep(300);
        Stream exceptionTopics = result.values().entrySet().stream().filter(keyValue -> {
            return keyValue.getValue().isCompletedExceptionally();
        });

        return exceptionTopics.map(exceptionTopic->{
            return exceptionTopic.toString();
        }).collect(Collectors.toList()).toString();
        // return adminClient.listTopics().names().get().toString();

    }
}
