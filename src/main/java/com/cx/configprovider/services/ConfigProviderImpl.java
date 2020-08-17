package com.cx.configprovider.services;

import com.cx.configprovider.dto.ConfigObject;
import com.cx.configprovider.dto.interfaces.ConfigProperties;
import com.cx.configprovider.services.interfaces.ConfigProvider;
import com.cx.configprovider.services.interfaces.ConfigSource;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

public class ConfigProviderImpl implements ConfigProvider {

    Map<String, JsonNode > configurationMap = new HashMap<String, JsonNode >();


    @Override
    public void init(String uid, ConfigSource configSource){

        JsonNode jsonNode = configSource.parse();

        store(uid, jsonNode);

    }

    private void store(String uid, JsonNode jsonNode){

        configurationMap.put(uid, jsonNode);

    }

    @Override
    public ConfigObject getConfigObject(String uid, ConfigProperties propertiesToMap){

        return null;

    }


}
