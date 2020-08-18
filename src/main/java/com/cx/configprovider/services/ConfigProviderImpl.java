package com.cx.configprovider.services;

import com.cx.configprovider.dto.interfaces.ConfigResource;
import com.cx.configprovider.services.interfaces.ConfigProvider;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;

import javax.naming.ConfigurationException;
import java.util.HashMap;
import java.util.Map;

public class ConfigProviderImpl implements ConfigProvider {

    Map<String, Config> configurationMap = new HashMap<>();
    private String appName;

    @Override
    public void initBaseResource(String appName, ConfigResource configSource) throws ConfigurationException {

        this.appName = appName;
        loadResource(appName,configSource);
    }
    
    @Override
    public void mergeResources(String uid, ConfigResource configSource, ConfigObject configToMerge) throws ConfigurationException {

        Config config = configSource.parse();
        config.withFallback(configToMerge);
        store(uid, config);
    }

    @Override
    public void loadResource(String uid, ConfigResource configSource) throws ConfigurationException {

        Config config = configSource.parse();
        store(uid, config);
    }

    private void store(String uid, Config config){
        configurationMap.put(uid, config);
    }

    @Override
    public ConfigObject getConfigObject(String uid){
        return configurationMap.get(uid).root();
    }

    
    @Override
    public ConfigObject getConfigObjectSection(String uid, String section){
        return configurationMap.get(uid).root().atKey(section).root();
    }

    @Override
    public ConfigObject getBaseConfig() {
        return configurationMap.get(appName).root();
    }
}
