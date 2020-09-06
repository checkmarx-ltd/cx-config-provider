package com.checkmarx.configprovider.resource;

import com.checkmarx.configprovider.dto.interfaces.ConfigResource;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import javax.naming.ConfigurationException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class should be used with applying several Configuration Resources at the same time.
 * The elements will be applied based on the order ot their addition to the class,
 * unless there is a rule for the order of applying of specific element types
 */
public class MultipleResources implements ConfigResource {

    List<ParsableResource> configSourceList = new LinkedList<>();

    public MultipleResources(){
    }

    public MultipleResources(ParsableResource... resources) {
        add(Arrays.asList(resources));
    }

    public MultipleResources(List<ParsableResource> configResources){
        add(configResources);
    }
    
    public void add(List<ParsableResource> configResources){
        configSourceList.addAll(configResources);
        applyOrder();
    }
    
    public void add(ParsableResource configSource){
        configSourceList.add(configSource);
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
     * same name and path will be overridden by the same element as per
     * the applied hierarchy
     *
     * @return Config tree
     */
    public Config load() throws ConfigurationException {
        Config configFull = ConfigFactory.empty();
        for (ParsableResource configSource : configSourceList) {
            Config configCurrent = configSource.load();
            configFull = configCurrent.withFallback(configFull);
        }
        return configFull;
    }

    @Override
    public String getName() {
        return configSourceList.stream().map(resource -> ((ConfigResource)resource).getName().concat(" ")).collect(Collectors.toList()).toString();
    }
}
