package com.cx.configprovider.resource;

import com.cx.configprovider.dto.ResourceType;
import com.cx.configprovider.dto.interfaces.ConfigResource;
import com.typesafe.config.Config;
import lombok.Getter;

import java.net.URL;

/**
 * Represents a non-parsed ("raw") config-as-code.
 */


@Getter
public class URLResource extends AbstractFileResource implements ConfigResource {
 
    private URL url;

    
    public URLResource(ResourceType type, URL url) {
        this.type = type;
        this.url = url;
    }
    
    
    @Override
    Config loadConfig() {
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
