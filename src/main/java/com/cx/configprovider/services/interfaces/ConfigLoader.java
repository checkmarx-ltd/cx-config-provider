package com.cx.configprovider.services.interfaces;

import com.cx.configprovider.dto.ConfigLocation;
import com.cx.configprovider.dto.ConfigResource;

import javax.naming.ConfigurationException;

public interface ConfigLoader {
    public ConfigResource getConfigAsCode(ConfigLocation configLocation) throws ConfigurationException;
}
