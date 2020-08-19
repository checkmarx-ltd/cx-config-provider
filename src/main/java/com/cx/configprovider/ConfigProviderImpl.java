package com.cx.configprovider;

import com.cx.configprovider.dto.interfaces.ConfigResource;
import com.cx.configprovider.interfaces.ConfigProvider;
import com.cx.configprovider.resource.MultipleResourcesImpl;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;


import javax.naming.ConfigurationException;
import java.util.HashMap;
import java.util.Map;

public class ConfigProviderImpl implements ConfigProvider {

    Map<String, Config> configurationMap = new HashMap<>();
    private String appName;

    @Override
    public void initBaseResource(String appName, ConfigResource configSource) throws ConfigurationException {

        this.appName = appName;
        Config config = configSource.parse();
        store(appName, config);
    }
    
    @Override
    public void mergeResources(String uid, ConfigResource configSource, ConfigObject configToMerge) throws ConfigurationException {

        Config newConfig = configSource.parse();
        Config mergedConfig = newConfig.withFallback(configToMerge);
        Config mergedWithBase = mergedConfig.withFallback(getBaseConfig());
        store(uid, mergedWithBase);
    }

    /**
     * Applies elements from input configResource upon base resource 
     * elements already stored in the the ConfigProvider. Elements from
     * the input ConfigResource with the same name and xpath will truncate
     * those form the base resource.
     * @param uid
     * @param configResource containing a representation of one or several
     *        configuration files. In case of multiple files their will
     *        be applied according to a policy. See {@link MultipleResourcesImpl}             
     * @throws ConfigurationException
     */
    @Override
    public void loadResource(String uid, ConfigResource configResource) throws ConfigurationException {

        ConfigObject baseConfig = getBaseConfig();
        Config configToMerge = configResource.parse();
        Config mergedConfig = configToMerge.withFallback(baseConfig);
        store(uid, mergedConfig);
    }

    private void store(String uid, Config config){
        configurationMap.put(uid, config);
    }

    @Override
    public ConfigObject getConfigObject(String uid){
        return configurationMap.get(uid).root();
    }

    
    @Override
    public ConfigObject getConfigObjectSection(String uid, String section){
        return configurationMap.get(uid).root().atKey(section).root();
    }

    @Override
    public ConfigObject getBaseConfig() {
        return configurationMap.get(appName).root();
    }
}
