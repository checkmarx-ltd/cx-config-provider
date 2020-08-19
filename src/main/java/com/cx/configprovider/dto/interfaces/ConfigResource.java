package com.cx.configprovider.dto.interfaces;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigParseOptions;

import javax.naming.ConfigurationException;

public interface ConfigResource {

    /**
     * Coverts ConfigResource to a configuration tree
     * as places each element in the tree based on its 
     * xpath
     * @return Config tree
     * @throws ConfigurationException
     */
    Config parse() throws ConfigurationException;
}
