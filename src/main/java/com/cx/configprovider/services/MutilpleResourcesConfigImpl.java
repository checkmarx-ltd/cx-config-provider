package com.cx.configprovider.services;

import com.cx.configprovider.dto.interfaces.ConfigResource;
import com.typesafe.config.Config;

import javax.naming.ConfigurationException;
import java.util.LinkedList;
import java.util.List;

public class MutilpleResourcesConfigImpl implements ConfigResource {

    List<ConfigResource> configSourceList = new LinkedList<>();

    public MutilpleResourcesConfigImpl add(ConfigResource ConfigSource){
        configSourceList.add(ConfigSource);
        applyOrder();
        return this;
    }

    private void applyOrder() {
        //TO IMPLEMENT
    }
    
    
    @Override
    public Config parse() throws ConfigurationException {

        Config configFull = null;
        for (ConfigResource configSource : configSourceList ) {
            if(configFull == null){
                configFull = configSource.parse();
            }else{
                Config configCurrent = configSource.parse();
                configFull.withFallback(configCurrent);
            }

        }

        return configFull;
    }
}
