package com.checkmarx.configprovider.readers;

import com.checkmarx.configprovider.dto.interfaces.ConfigReader;
import com.typesafe.config.Config;

import javax.naming.ConfigurationException;

public class Processor {
    private Processor() {}

    public static Config load(ConfigReader reader) throws ConfigurationException {
        return ((Parsable)reader).toConfig();
    }
}
