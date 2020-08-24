package com.cx.configprovider.resource;

import com.cx.configprovider.dto.ResourceType;
import com.typesafe.config.Config;
import lombok.Getter;

import javax.naming.ConfigurationException;
import java.net.URL;

/**
 * Represents a non-parsed ("raw") config-as-code.
 */


@Getter
public class URLResourceImpl extends ConfigResourceImpl {
 
    private URL url;

    
    public void URLResourceImpl(ResourceType type, URL url) throws ConfigurationException {
        this.type = type;
        this.url = url;
    }
    
    
    @Override
    public Config parse() throws ConfigurationException {
        if(ResourceType.YML.equals(type)){
            config = yamlToConfig(url);
        }else if(ResourceType.JSON.equals(type)){
            config = jsonToConfig(url);
        }
        return config;
    }

    @Override
    public String getName() {
        return url.getPath();
    }


}
