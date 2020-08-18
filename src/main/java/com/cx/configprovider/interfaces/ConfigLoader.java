package com.cx.configprovider.interfaces;

import com.cx.configprovider.dto.ConfigLocation;
import com.cx.configprovider.dto.interfaces.ConfigResource;

import javax.naming.ConfigurationException;

public interface ConfigLoader {
    public ConfigResource getConfigAsCode(ConfigLocation configLocation) throws ConfigurationException;
}
