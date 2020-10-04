package com.checkmarx.configprovider;

import com.checkmarx.configprovider.dto.interfaces.ConfigReader;
import com.checkmarx.configprovider.exceptions.ConfigProviderException;
import com.checkmarx.configprovider.readers.ListReaders;
import com.checkmarx.configprovider.readers.Processor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigException;
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

    public boolean hasAnyConfiguration(String uid) {
        return Optional.ofNullable(configurationMap.get(uid))
                .map(Config::root)
                .map(ConfigObject::toConfig)
                .map(config -> !config.isEmpty())
                .orElse(false);
    }

    public boolean hasConfiguration(String uid, String configSection) {
        return Optional.ofNullable(configurationMap.get(uid))
                .map(Config::root)
                .map(ConfigObject::toConfig)
                .map(config -> config.hasPath(configSection))
                .orElse(false);
    }

    /**
     *  Use the @com.typesafe.config.Optional annotation to mark a field which may not be part of the configuration
     * @param uid id of the configuration
     * @param configSection the section that represents the class in the configuration
     * @param clazz the bean's class to initiate
     * @param <T> the bean class
     * @return an initiated bean of type {@code clazz} with the values from section  {@code configSection} of the configuration {@code uid}
     */
    public <T extends Object> T getConfiguration(String uid, String configSection, Class<T> clazz) {
        if (!hasConfiguration(uid, configSection)) {
            log.warn("No matching configuration for {} {}. returning null", uid, configSection);
            return null;
        }

        log.info("reading configuration {} into class {}", uid, clazz.getSimpleName());
        Config config = configurationMap.get(uid).root().toConfig();
        if (!config.isResolved()) {
            log.warn("reading configuration ({}) forced resolving the configuration", uid);
            config = config.resolve();
            store(uid, config);
        }
        config = config.getConfig(configSection);
        try {
            return ConfigBeanFactory.create(config, clazz);

        } catch (ConfigException e) {
            throw new ConfigProviderException(e.getMessage());
        }
    }


    /**
     * removes all cached config instances including the base instance
     */
    public void clear(){
        configurationMap.clear();
    }
}
