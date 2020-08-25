package com.cx.configprovider.interfaces;

import com.cx.configprovider.dto.interfaces.ConfigResource;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;

import javax.naming.ConfigurationException;
import java.util.List;
import java.util.Map;

public interface ConfigProvider {

    Config initBaseResource(String appName, ConfigResource configSource) throws ConfigurationException;

    ConfigObject loadResources(String uid, ConfigResource configSource, ConfigObject configToMerge) throws ConfigurationException;

    ConfigObject loadResource(String uid, ConfigResource configSource) throws ConfigurationException;

    ConfigObject getConfigObject(String uid);

    String getStringValue(String uid, String path);

    Map getMapFromConfig(String uid, String path);

    List<String> getStringListFromConfig(String uid, String xpath);

    ConfigObject getConfigObjectSection(String uid, String section);

    ConfigObject getBaseConfig();

    void clear();
}
