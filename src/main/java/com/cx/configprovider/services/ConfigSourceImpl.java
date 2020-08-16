package com.cx.configprovider.services;

import com.cx.configprovider.services.interfaces.ConfigSource;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedList;
import java.util.List;

public class ConfigSourceImpl implements ConfigSource {

    List<ConfigSource> configSourceList = new LinkedList<>();

    private List<String>getConfigurationStrList(){

        List<String> configSourceStrList  = new LinkedList<>();

        for(ConfigSource configSource : configSourceList){

            String configurationStr = configSource.getConfigurationStr();

            configSourceStrList.add(configurationStr );

        }

        return configSourceStrList;

    }

    @Override
    public String getConfigurationStr() {
        return null;
    }

    @Override
    public JsonNode parseToJson(){

        List<String> list = getConfigurationStrList();

        return parse(list);

    }

    private JsonNode parse(List<String>list ){
          return null;
    }
}
