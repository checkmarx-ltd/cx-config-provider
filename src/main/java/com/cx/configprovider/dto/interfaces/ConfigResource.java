package com.cx.configprovider.dto.interfaces;

import com.typesafe.config.Config;

import javax.naming.ConfigurationException;

public interface ConfigResource {

    /**
     * Converts ConfigResource to a configuration tree
     * as places each element in the tree based on its 
     * path
     * @return a parsed Config tree representing the ConfigResource
     * @throws ConfigurationException exception
     */
    Config parse() throws ConfigurationException;
}
