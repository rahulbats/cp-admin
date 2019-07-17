package com.confluent.admin.service;

import com.confluent.admin.model.Acl;
import com.confluent.admin.model.Topic;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateAclsResult;
import org.apache.kafka.common.acl.AccessControlEntry;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.acl.AclPermissionType;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourcePattern;
import org.apache.kafka.common.resource.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ACLService {
    @Autowired
    private AdminClient adminClient;

    public String createACL(@RequestBody List<Acl> acls) throws Exception {

       List<AclBinding> bindings = acls.stream().map(acl->{
            AccessControlEntry aclEntry = new AccessControlEntry(acl.getPrincipal(),"*",acl.getOperation(), acl.getPermission());
            ResourcePattern pattern = new ResourcePattern(acl.getResourceType(), acl.getResourceName(), acl.getPatternType());
            return new AclBinding( pattern, aclEntry);
        }).collect(Collectors.toList());

        CreateAclsResult result = adminClient.createAcls(bindings);
        while (!result.all().isDone())
            Thread.sleep(300);
        Stream exceptionACLs = result.values().entrySet().stream().filter(keyValue -> {
            return keyValue.getValue().isCompletedExceptionally();
        });

        return exceptionACLs.map(exceptionACL->{
            return exceptionACL.toString();
        }).collect(Collectors.toList()).toString();
    }
}
