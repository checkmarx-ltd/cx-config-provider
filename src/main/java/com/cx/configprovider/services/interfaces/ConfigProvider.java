package com.cx.configprovider.services.interfaces;

import com.cx.configprovider.dto.interfaces.ConfigResource;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;

import javax.naming.ConfigurationException;

public interface ConfigProvider {

    void initBaseResource(String appName, ConfigResource configSource) throws ConfigurationException;

    void mergeResources(String uid, ConfigResource configSource, ConfigObject configToMerge) throws ConfigurationException;

    void loadResource(String uid, ConfigResource configSource) throws ConfigurationException;

    ConfigObject getConfigObject(String uid);

    ConfigObject getConfigObjectSection(String uid, String section);

    ConfigObject getBaseConfig();
}
