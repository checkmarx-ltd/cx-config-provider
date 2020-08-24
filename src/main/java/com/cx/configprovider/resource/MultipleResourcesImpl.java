package com.cx.configprovider.resource;

import com.cx.configprovider.dto.interfaces.ConfigResource;
import com.typesafe.config.Config;

import javax.naming.ConfigurationException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class should be used with applying several Configuration Resources at the same time.
 * The elements will be applied based on the order ot their addition to the class,
 * unless there is a rule for the order of applying of specific element types
 */
public class MultipleResourcesImpl implements ConfigResource {

    List<ConfigResource> configSourceList = new LinkedList<>();

    public MultipleResourcesImpl(){
    }
    
    public MultipleResourcesImpl(List<ConfigResource> configResources){
        add(configResources);
    }
    
    public void add(List<ConfigResource> configResources){
        configSourceList.addAll(configResources);
        applyOrder();
    }
    
    public void add(ConfigResource ConfigSource){
        configSourceList.add(ConfigSource);
        applyOrder();
    }

    private void applyOrder() {
        //TO IMPLEMENT
        
        //Env Yaml
        //application.yml
        //config-as-code
    }

    /**
     * Converts a list on configSources added to the class to a config
     * tree while applying a specified order. An element which has the 
     * same name and path will be truncated by the same element as per 
     * the applied hierarchy
     * @return Config tree
     * @throws ConfigurationException exception
     */
    @Override
    public Config parse() throws ConfigurationException {

        Config configFull = null;
        for (ConfigResource configSource : configSourceList ) {
            if(configFull == null){
                configFull = configSource.parse();
            }else{
                Config configCurrent = configSource.parse();
                configFull = configCurrent.withFallback(configFull);
            }

        }

        return configFull;
    }

    @Override
    public String getName() {
        return configSourceList.stream().map(resource -> resource.getName().concat(" ")).collect(Collectors.toList()).toString();
    }

}
