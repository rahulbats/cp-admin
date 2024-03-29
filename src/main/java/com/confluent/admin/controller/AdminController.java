package com.confluent.admin.controller;

import com.confluent.admin.model.Acl;
import com.confluent.admin.model.Topic;
import com.confluent.admin.service.ACLService;
import com.confluent.admin.service.TopicService;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class AdminController {

    @Autowired
    TopicService topicService;

    @Autowired
    ACLService aclService;

    @RequestMapping(value = "/topics",  produces = "application/json")
    public Set<String> getTopics(@RequestAttribute("basePrefix") String basePrefix) throws Exception{

        return topicService.getTopicNames(basePrefix);

    }

    @RequestMapping(value = "/topics", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> createTopic(@RequestBody List<Topic> topics, @RequestAttribute("basePrefix") String basePrefix) throws Exception{

       return topicService.manageTopic(topics,false, basePrefix);

    }

    @RequestMapping(value = "/topics", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> deleteTopics(@RequestBody List<String> topics) throws Exception{

        return topicService.deleteTopic(topics);

    }

    @RequestMapping(value = "/acls", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String createACL(@RequestBody List<Acl> acls) throws Exception{

        return aclService.manageACL(acls);

    }
}