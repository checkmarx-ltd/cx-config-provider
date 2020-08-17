package com.cx.configprovider.services.interfaces;

import com.cx.configprovider.dto.interfaces.ConfigResource;
import com.typesafe.config.ConfigObject;

import javax.naming.ConfigurationException;

public interface ConfigProvider {

    void init(String uid, ConfigResource configSource) throws ConfigurationException;

    ConfigObject getConfigObject(String uid);

    ConfigObject getConfigObjectSection(String uid, String section);
}
