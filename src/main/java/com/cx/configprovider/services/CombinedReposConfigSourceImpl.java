package com.cx.configprovider.services;

import com.cx.configprovider.services.interfaces.ConfigSource;
import com.typesafe.config.Config;

import java.util.LinkedList;
import java.util.List;

public class CombinedReposConfigSourceImpl implements ConfigSource {

    List<ConfigSource> configSourceList = new LinkedList<>();

    public CombinedReposConfigSourceImpl(){};

    public CombinedReposConfigSourceImpl build(ConfigSource ConfigSource){
        configSourceList.add(ConfigSource);
        applyOrder();
        return this;
    }

    private void applyOrder() {
        //TO IMPLEMENT
    }


    @Override
    public Config getConfig(){

        Config configFull = null;
        for (ConfigSource configSource : configSourceList ) {
            if(configFull == null){
                configFull = configSource.getConfig();
            }else{
                Config configCurrent = configSource.getConfig();
                configFull.withFallback(configCurrent);
            }
            
        }
        
        return configFull;
        
    }
    
}
