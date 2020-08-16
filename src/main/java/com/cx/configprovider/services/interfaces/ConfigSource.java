package com.cx.configprovider.services.interfaces;

import com.fasterxml.jackson.databind.JsonNode;

public interface ConfigSource {
    
    String getConfigurationStr();

    JsonNode parseToJson();
}
