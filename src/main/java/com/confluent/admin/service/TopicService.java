package com.confluent.admin.service;

import com.confluent.admin.model.Topic;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.config.ConfigResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class TopicService {
    @Autowired
    private AdminClient adminClient;


    @Value("${delete.enable}")
    private String deleteString;

    public Set<String> getTopicNames() throws InterruptedException, ExecutionException {
        ListTopicsOptions listTopicsOptions = new ListTopicsOptions();
        listTopicsOptions.listInternal(true);
        return adminClient.listTopics(listTopicsOptions).names().get();
    }

    public List<TopicDescription> getTopicsWithPartitions() throws InterruptedException, ExecutionException {
        return adminClient.describeTopics(getTopicNames()).values().values().stream().map(existingTopic->{
            try {
                return existingTopic.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            } catch (ExecutionException e) {
                e.printStackTrace();
                return  null;
            }
        }).filter(existingTopic->{
            return !(existingTopic==null || existingTopic.isInternal() || existingTopic.name().startsWith("_confluent"));
        })
                .collect(Collectors.toList());
    }

    public Map<String,Config> getTopicsWithConfigs() throws InterruptedException, ExecutionException {
        Map<String,Config> configs = new HashMap<>();
        adminClient.describeConfigs(getTopicsWithPartitions().stream().map(topic->new ConfigResource(ConfigResource.Type.TOPIC, topic.name())).collect(Collectors.toList())).values().entrySet().stream()
                .forEach(existingTopic->{
            try {
                configs.put(existingTopic.getKey().name(),existingTopic.getValue().get());
            } catch (InterruptedException e) {
                e.printStackTrace();

            } catch (ExecutionException e) {
                e.printStackTrace();

            }
        });
        return configs;
    }



    public List<String> manageTopic(@RequestBody List<Topic> topics) throws Exception{

        List<TopicDescription> existingTopicDescriptions = getTopicsWithPartitions();

        Map<String, List<TopicPartitionInfo>> existingTopicNamesPartitions =
                existingTopicDescriptions.stream().collect(Collectors.toMap(TopicDescription::name, TopicDescription::partitions));

        List<NewTopic> newTopics = topics.stream()
                .filter(topic -> {
                    return !existingTopicNamesPartitions.keySet().contains(topic.getName());
                })
                .map(topic -> {
                    NewTopic newTopic = new NewTopic(topic.getName(), topic.getNumPartitions(), topic.getReplicationFactor());
                    newTopic.configs(topic.getConfigs());
                    return newTopic;
                }).collect(Collectors.toList());

        Map<String, NewPartitions> alterTopicPartitions = topics.stream()
                .filter(topic -> {
                    boolean alter = false;
                    if(existingTopicNamesPartitions.keySet().contains(topic.getName()))
                       alter = existingTopicNamesPartitions.get(topic.getName()).size() != topic.getNumPartitions();

                    return alter;
                }).collect(Collectors.toMap(Topic::getName, topic ->  NewPartitions.increaseTo(topic.getNumPartitions())));



        List<String> topicNames = topics.stream().map(topic -> topic.getName()).collect(Collectors.toList());



        CreateTopicsResult result = adminClient.createTopics(newTopics);
        while (!result.all().isDone())
            Thread.sleep(300);

        List<String> exceptionTopics = result.values().entrySet().stream().filter(keyValue -> {
            return keyValue.getValue().isCompletedExceptionally();
        }).map(exceptionTopic->{
            return exceptionTopic.toString();
        }).collect(Collectors.toList());

        CreatePartitionsResult createPartitionsResult = adminClient.createPartitions(alterTopicPartitions);
        while (!createPartitionsResult.all().isDone())
            Thread.sleep(300);

        List<String> exceptionPartitionTopics = createPartitionsResult.values().entrySet().stream().filter(keyValue -> {
            return keyValue.getValue().isCompletedExceptionally();
        }).map(exceptionTopic->{
            return exceptionTopic.toString();
        }).collect(Collectors.toList());
        exceptionTopics.addAll(exceptionPartitionTopics);


        /*Map<String, Config> configs = getTopicsWithConfigs();


        Map<ConfigResource, Collection<AlterConfigOp>> alterConfigs = new HashMap<>();
        topics.stream().forEach(topic -> {
            Config existingEntriesConfig =  configs.get(topic.getName());
            Map<String,String> existingEntries = existingEntriesConfig.entries().stream().collect(Collectors.toMap(ConfigEntry::name, ConfigEntry::value));
            Map<String,String> incomingEntries = topic.getConfigs();
            Map<String,String> filteredConfigs =incomingEntries.entrySet().stream().filter(entry -> !existingEntries.get(entry.getKey()).equals(entry.getValue())).collect(Collectors.toMap(p->p.getKey(),p->p.getValue()));
            //Collection<AlterConfigOp> = new
            List<AlterConfigOp> configOps = filteredConfigs.entrySet().stream().map(entry->{
                ConfigEntry configEntry = new ConfigEntry(entry.getKey(), entry.getValue());
                AlterConfigOp alterConfigOp = new AlterConfigOp(configEntry, AlterConfigOp.OpType.SET);
                return alterConfigOp;
            }).collect(Collectors.toList());
            alterConfigs.put(new ConfigResource(ConfigResource.Type.TOPIC, topic.getName()), configOps);
        });

        AlterConfigsResult alterConfigsResult = adminClient.incrementalAlterConfigs(alterConfigs);
        while (!alterConfigsResult.all().isDone())
            Thread.sleep(300);
        List<String> exceptionConfigTopics = alterConfigsResult.values().entrySet().stream().filter(keyValue -> {
            return keyValue.getValue().isCompletedExceptionally();
        }).map(exceptionTopic->{
            return exceptionTopic.toString();
        }).collect(Collectors.toList());
        exceptionTopics.addAll(exceptionConfigTopics);*/


        if(new Boolean(deleteString)) {
            List<String> deleteTopics = existingTopicNamesPartitions.keySet().stream().filter(existingTopicName->!topicNames.contains(existingTopicName)).collect(Collectors.toList());
            DeleteTopicsResult deleteTopicsResult = adminClient.deleteTopics(deleteTopics);
            while (!deleteTopicsResult.all().isDone())
                Thread.sleep(300);

            List<String> exceptionDeleteTopics = deleteTopicsResult.values().entrySet().stream().filter(keyValue -> {
                return keyValue.getValue().isCompletedExceptionally();
            }).map(exceptionTopic->{
                return exceptionTopic.toString();
            }).collect(Collectors.toList());
            exceptionTopics.addAll(exceptionDeleteTopics);
        }



        System.out.println(exceptionTopics.toString());

        return exceptionTopics;

    }


}
