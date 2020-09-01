package com.checkmarx.configprovider.resource;

import com.checkmarx.configprovider.dto.interfaces.ConfigResource;
import com.checkmarx.configprovider.dto.ResourceType;
import com.typesafe.config.Config;
import lombok.Getter;

import java.net.URL;




@Getter
public class URLResource extends AbstractFileResource implements ConfigResource {
 
    private URL url;

    
    public URLResource(ResourceType type, URL url) {
        this.type = type;
        this.url = url;
    }


    /**
     * Currently not implemented - throws UnsupportedOperationException
     * @return
     */
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
