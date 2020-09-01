package com.checkmarx.configprovider.resource;

import com.checkmarx.configprovider.dto.interfaces.ConfigResource;
import com.typesafe.config.Config;

import javax.naming.ConfigurationException;

public class Parser {
    private Parser() {}

    public static Config parse(ConfigResource resource) throws ConfigurationException {
        return ((ParsableResource)resource).loadConfig();
    }
}
