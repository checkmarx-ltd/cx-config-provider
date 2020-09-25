package com.checkmarx.configprovider;

import com.checkmarx.configprovider.annotations.AnnotationsHandler;
import com.checkmarx.configprovider.dto.interfaces.ConfigReader;
import com.checkmarx.configprovider.readers.ListReaders;
import com.checkmarx.configprovider.readers.Processor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;

import lombok.extern.slf4j.Slf4j;

import javax.naming.ConfigurationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class ConfigProvider {

    Map<String, Config> configurationMap = new HashMap<>();

    private static ConfigProvider instance = null;

    private ConfigProvider(){}
    
    public static ConfigProvider getInstance(){
        return Optional.ofNullable(instance).orElseGet(() -> {
            instance = new ConfigProvider();
            return instance;
        });
    }
    

    /**
     * Converts a one or list of configuration files/locations into one 
     * hierarchical configuration. See {@link ConfigReader} and its implementations.
     * For list use {@link ListReaders} {@link ConfigReader} implementation.
     * Configuration elements with the same name and path will be overridden.
     * See override logic in {@link ListReaders}
     * @param uid a unique identifier of a ConfigObject to be created
     * @param reader containing a representation of one or several
     *        configuration files. In case of multiple files their will
     *        be applied according to a policy. See {@link ListReaders}
     * @throws ConfigurationException exception
     */
    public void init(String uid, ConfigReader reader) throws ConfigurationException {
        
        Config config = Processor.load(reader);
        store(uid, config);
    }

    private void store(String uid, Config config){
        configurationMap.put(uid, config);
    }


    /**
     * @deprecated
     * use {@link #getConfiguration(String, Object)}
     * @param uid
     * @return
     */
    @Deprecated
    public ConfigObject getConfigObject(String uid){
        return Optional.ofNullable(configurationMap.get(uid)).map(Config::root)
        .orElse(null);
    }

    public <T extends Object> T getConfiguration(String uid , T object) {
        Optional<String> treePath = AnnotationsHandler.getCofigurationTreePath(object.getClass());
        if (treePath.isPresent() && treePath.get().isEmpty()) {
            log.warn("class {} does not have an associated configuration path, using root", object.getClass().getName());
        }

        Optional<Config> config = Optional.ofNullable(configurationMap.get(uid));
        config.ifPresent(conf -> {
            Config currentConfig = treePath.map(conf::getConfig).orElse(conf.root().toConfig());
            AnnotationsHandler.setValuesByConfiguration(object , currentConfig);
        });
        return object;
    }
    

    /**
     * removes all cached config instances including the base instance
     */
    public void clear(){
        configurationMap.clear();
    }
}
