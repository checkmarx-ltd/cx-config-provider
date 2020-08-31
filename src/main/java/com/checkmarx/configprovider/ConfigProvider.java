package com.cx.configprovider;

import com.cx.configprovider.resource.MultipleResources;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValueType;
import lombok.Getter;

import javax.naming.ConfigurationException;
import java.util.HashMap;
import java.util.List;
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
     * @return ConfigObject representing a configuration tree
     */
    public ConfigObject initConfig(String uid, MultipleResources resources) throws ConfigurationException {
        Config contextSpecificConfig = resources.load();
        Config mergedConfig = contextSpecificConfig.withFallback(baseConfig);
        store(uid, mergedConfig);
        return mergedConfig.root();
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


    public Map<String, Object> getMapFromConfig(String uid, String pathToMap) {
        Object result = getConfigObject(uid).toConfig().getValue(pathToMap).unwrapped();

        if (result instanceof Map) {
            return (Map) result;
        } else {
            throw new TypeNotPresentException("Map", null);
        }

    }

    public List<String> getStringListFromConfig(String uid, String pathToList) {
        ConfigValueType type = getConfigObject(uid).toConfig().getValue(pathToList).valueType();
        if (type.equals(ConfigValueType.LIST)) {
            return getConfigObject(uid).toConfig().getStringList(pathToList);
        } else {
            throw new TypeNotPresentException("List", null);
        }
    }


    public ConfigObject getConfigObjectSection(String uid, String section) {
        return getConfigObject(uid).atKey(section).root();
    }


    public ConfigObject getBaseConfig() {
        return baseConfig.root();
    }

    /**
     * removes all cached config instances including the base instance
     */
    public void clear() {
        configurationMap.clear();
    }
}
