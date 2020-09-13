package com.checkmarx.configprovider.dto;

import java.util.Arrays;
import java.util.List;

public enum ResourceType {
    YAML("yaml", "yml"), JSON("json"), PROPERTIES("properties"), ENV_VARIABLES("env"), COMBINED();

    private List<String> fileExtentions;

    private ResourceType(String... fileExtentions) {
        this.fileExtentions = Arrays.asList(fileExtentions);
    }

    public ResourceType getTypeByExtention(String extention) {
        return Arrays.stream(values())
            .filter(type -> type.fileExtentions.contains(extention)).findAny()
            .orElse(COMBINED);
        
    }
}
