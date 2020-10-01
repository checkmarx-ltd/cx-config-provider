package com.checkmarx.configprovider.dto;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum ResourceType {
    YAML("yaml", "yml", "configuration"), JSON("json", "config"), PROPERTIES("properties"), ENV_VARIABLES("env"), COMBINED();

    private List<String> fileExtentions;

    private ResourceType(String... fileExtentions) {
        this.fileExtentions = Arrays.asList(fileExtentions);
    }

    public static ResourceType getTypeByNameOrExtention(String nameOrExtention) {
        String extention = nameOrExtention.substring(nameOrExtention.lastIndexOf('.')+1);
        log.info("resolving extention for {}", nameOrExtention);
        return Arrays.stream(values())
            .filter(type -> type.fileExtentions.contains(extention.toLowerCase().trim())).findAny()
            .map(res -> {
                log.info("extention {} is {}", extention, res);
                return res;
            })
            .orElseGet( () -> {
                log.info("extention {} is {}", extention, COMBINED);
                return COMBINED;
            });
        
    }
}
