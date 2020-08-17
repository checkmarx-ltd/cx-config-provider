package com.cx.configprovider.dto;

import com.cx.configprovider.dto.interfaces.ConfigResource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Getter;
import org.apache.commons.io.IOUtils;

import javax.naming.ConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Represents a non-parsed ("raw") config-as-code.
 */


@Getter
public class URLResourceImpl extends ConfigResourceImpl {
    private ResourceType type;
    private URL url;
    private Config config;
    
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

    
}
