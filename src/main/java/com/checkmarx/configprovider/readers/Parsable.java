package com.checkmarx.configprovider.readers;

import com.typesafe.config.Config;

import javax.naming.ConfigurationException;

/**
 * This is an internal resource to be used within the configuration provider package
 */
public abstract class Parsable {

    /**
     * Converts ConfigResource to a configuration tree
     * as places each element in the tree based on its 
     * path
     * @return a parsed Config tree representing the ConfigResource
     * @throws ConfigurationException exception
     */
     abstract Config toConfig() throws ConfigurationException;
    
}
