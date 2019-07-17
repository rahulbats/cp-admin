package com.confluent.admin.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.acl.AclPermissionType;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourceType;

@Data
public class Acl {
    @Getter @Setter private String principal;
    @Getter @Setter private AclPermissionType permission;
    @Getter @Setter private AclOperation operation;
    @Getter @Setter private ResourceType resourceType;
    @Getter @Setter private String resourceName;
    @Getter @Setter private PatternType patternType;


}
