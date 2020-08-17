package com.cx.configprovider.services.interfaces;

import com.cx.configprovider.dto.ConfigObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.Config;

public interface ConfigSource {
    
    Config getConfig();
}
