package com.cx.configprovider.resource;

import com.cx.configprovider.dto.interfaces.ConfigResource;
import com.typesafe.config.Config;

import javax.naming.ConfigurationException;

public class Parser {
    
    public static Config parse(ConfigResource resource) throws ConfigurationException {
        return ((ParsableResource)resource).loadConfig();
    }
}
