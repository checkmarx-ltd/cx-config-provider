package com.cx.configprovider.services.interfaces;

import com.cx.configprovider.dto.interfaces.ConfigResource;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;

import javax.naming.ConfigurationException;

public interface ConfigProvider {
    
    void merge(String uid, ConfigResource configSource, ConfigObject configToMerge) throws ConfigurationException;

    void load(String uid, ConfigResource configSource) throws ConfigurationException;

    ConfigObject getConfigObject(String uid);

    ConfigObject getConfigObjectSection(String uid, String section);
}
