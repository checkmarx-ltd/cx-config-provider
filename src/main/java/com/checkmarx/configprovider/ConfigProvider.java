package com.cx.configprovider;

import com.cx.configprovider.resource.MultipleResources;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import lombok.Getter;

import javax.naming.ConfigurationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConfigProvider {
    @Getter(lazy = true)
    private static final ConfigProvider instance = new ConfigProvider();

    private final Map<String, Config> configurationMap = new HashMap<>();

    private Config baseConfig;

    private ConfigProvider() {
    }

    public void initBaseConfig(MultipleResources resources) throws ConfigurationException {
        baseConfig = resources.load();
    }

    /**
     * Applies elements from input resources upon base resource
     * elements already stored in the the ConfigProvider. Elements from
     * the input ConfigResource with the same name and path will override
     * those from the base resource.
     *
     * @param uid       a unique identifier of a ConfigObject to be created
     * @param resources contains a representation of one or several
     *                  configuration sources.
     */
    public void initConfig(String uid, MultipleResources resources) throws ConfigurationException {
        Config contextSpecificConfig = resources.load();
        Config mergedConfig = contextSpecificConfig.withFallback(baseConfig);
        store(uid, mergedConfig);
    }

    private void store(String uid, Config config) {
        configurationMap.put(uid, config);
    }

    public ConfigObject getConfigObject(String uid) {
        return Optional.ofNullable(configurationMap.get(uid)).map(Config::root)
                .orElse(null);
    }

    public String getStringValue(String uid, String path) {
        return getConfigObject(uid).toConfig().getString(path);
    }

    /**
     * removes all cached config instances including the base instance
     */
    public void clear() {
        configurationMap.clear();
    }
}
