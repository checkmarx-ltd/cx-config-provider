package com.checkmarx.configprovider.dto;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum ResourceType {
    YAML("yaml", "yml"), JSON("json"), PROPERTIES("properties"), ENV_VARIABLES("env"), COMBINED();

    private List<String> fileExtentions;

    private ResourceType(String... fileExtentions) {
        this.fileExtentions = Arrays.asList(fileExtentions);
    }

    public static ResourceType getTypeByExtention(String extention) {
        UnaryOperator<ResourceType> logAndReturn = res -> {
            log.info("extention {} is {}", extention, res);
            return res;
        };
        log.info("resolving file type by extention");
        return Arrays.stream(values())
            .filter(type -> type.fileExtentions.contains(extention.toLowerCase())).findAny()
            .map(logAndReturn)
            .orElse(logAndReturn.apply(COMBINED));
        
    }
}
