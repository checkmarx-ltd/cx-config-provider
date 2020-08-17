package com.cx.configprovider.resource;

import com.cx.configprovider.dto.interfaces.ConfigResource;
import com.typesafe.config.Config;

import javax.naming.ConfigurationException;
import java.util.LinkedList;
import java.util.List;

public class MutilpleResourcesImpl implements ConfigResource {

    List<ConfigResource> configSourceList = new LinkedList<>();

    public MutilpleResourcesImpl add(ConfigResource ConfigSource){
        configSourceList.add(ConfigSource);
        applyOrder();
        return this;
    }

    private void applyOrder() {
        //TO IMPLEMENT
        
        //Env Yaml
        //application.yml
        //config-as-code
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
