package com.checkmarx.configprovider.readers;

import com.checkmarx.configprovider.dto.interfaces.ConfigReader;
import com.checkmarx.configprovider.dto.ResourceType;
import com.typesafe.config.Config;
import lombok.Getter;

import java.net.URL;




@Getter
public class URLReader extends AbstractFileReader implements ConfigReader {
 
    private URL url;

    
    public URLReader(ResourceType type, URL url) {
        this.type = type;
        this.url = url;
    }


    /**
     * Currently not implemented - throws UnsupportedOperationException
     * @return
     */
    @Override
    Config toConfig() {
        if(ResourceType.YAML.equals(type)){
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
