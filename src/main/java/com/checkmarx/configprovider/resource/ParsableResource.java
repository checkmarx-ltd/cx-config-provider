package com.checkmarx.configprovider.resource;

import com.typesafe.config.Config;

/**
 * This is an internal resource to be used within the configuration provider package
 */
public abstract class ParsableResource {

    /**
     * Converts ConfigResource to a configuration tree
     * as places each element in the tree based on its 
     * path
     * @return a parsed Config tree representing the ConfigResource
     */
     abstract Config load();
}
