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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public Set<String> getTopics() throws Exception{

        return topicService.getTopicNames();

    }

    @RequestMapping(value = "/topics", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String createTopic(@RequestBody List<Topic> topics) throws Exception{

       return topicService.createTopic(topics);

    }

    @RequestMapping(value = "/acls", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String createACL(@RequestBody List<Acl> acls) throws Exception{

        return aclService.createACL(acls);

    }
}