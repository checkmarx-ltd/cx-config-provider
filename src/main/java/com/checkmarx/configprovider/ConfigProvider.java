package com.checkmarx.configprovider;

import com.checkmarx.configprovider.dto.interfaces.ConfigResource;

import com.checkmarx.configprovider.resource.MultipleResources;
import com.checkmarx.configprovider.resource.Parser;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValueType;


import javax.naming.ConfigurationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ConfigProvider {

    Map<String, Config> configurationMap = new HashMap<>();
    private String appName;

    private static ConfigProvider instance = null;

    private ConfigProvider(){}
    
    public static ConfigProvider getInstance(){
        return Optional.ofNullable(instance).orElseGet(() -> {
            instance = new ConfigProvider();
            return instance;
        });
    }

    public Config initBaseResource(String appName, ConfigResource configSource) throws ConfigurationException {

        this.appName = appName;
        Config config = Parser.parse(configSource);
        store(appName, config);
        return config;
    }

    /**
     * Loads resources using a previously loaded configuration object and a new resource containing one 
     * or several configuration files / locations
      @deprecated
     * It is not recommended to use this method.
     * <p> Use {@link #loadResource(String , ConfigResource )} with {@link MultipleResources} instead.
     * @param uid a unique identifier of a ConfigObject to be created
     * @param configSource a resource containing one or multiple configuration files / locations to be loaded 
     * @param configToMerge configuration object which is a result of previous load
     * @return ConfigObject representing a merged configuration tree
     * @throws ConfigurationException
     */
    @Deprecated
    public ConfigObject loadResources(String uid, ConfigResource configSource, ConfigObject configToMerge) throws ConfigurationException {

        Config newConfig = Parser.parse(configSource);
        Config mergedConfig = newConfig.withFallback(configToMerge);
        Config mergedWithBase = mergedConfig.withFallback(getBaseConfig());
        store(uid, mergedWithBase);
        return mergedWithBase.root();
    }

    /**
     * Applies elements from input configResource upon base resource 
     * elements already stored in the the ConfigProvider. Elements from
     * the input ConfigResource with the same name and path will override
     * those form the base resource.
     * @param uid a unique identifier of a ConfigObject to be created
     * @param configResource containing a representation of one or several
     *        configuration files. In case of multiple files their will
     *        be applied according to a policy. See {@link MultipleResources}
     * @throws ConfigurationException exception
     * @return ConfigObject representing a configuration tree
     */
    public ConfigObject loadResource(String uid, ConfigResource configResource) throws ConfigurationException {

        ConfigObject baseConfig = getBaseConfig();
        Config configToMerge = Parser.parse(configResource);
        Config mergedConfig = configToMerge.withFallback(baseConfig);
        store(uid, mergedConfig);
        return mergedConfig.root();
    }

    private void store(String uid, Config config){
        configurationMap.put(uid, config);
    }


    public ConfigObject getConfigObject(String uid){
        return Optional.ofNullable(configurationMap.get(uid)).map(Config::root)
        .orElse(null);
    }


 
    public String getStringValue(String uid, String path){
        return getConfigObject(uid).toConfig().getString(path);
    }


    public Map<String, Object> getMapFromConfig(String uid, String pathToMap){

        Object result = getConfigObject(uid).toConfig().getValue(pathToMap).unwrapped();
        
        if(result instanceof Map){
            return (Map)result;
        }else{
            throw new TypeNotPresentException("Map", null);
        }

    }


    public List<String> getStringListFromConfig(String uid, String pathToList){

        ConfigValueType type = getConfigObject(uid).toConfig().getValue(pathToList).valueType();
        if(type.equals(ConfigValueType.LIST)){
            return getConfigObject(uid).toConfig().getStringList(pathToList);
        }else{
            throw new TypeNotPresentException("List", null);
        }

    }
    

    public ConfigObject getConfigObjectSection(String uid, String section){
        return getConfigObject(uid).atKey(section).root();
    }

  
    public ConfigObject getBaseConfig() {
        return configurationMap.get(appName).resolve().root();
    }

    /**
     * removes all cached config instances including the base instance
     */
    public void clear(){
        configurationMap.clear();
    }
}
