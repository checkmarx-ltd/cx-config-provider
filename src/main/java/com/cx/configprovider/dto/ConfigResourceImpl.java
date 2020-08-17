package com.cx.configprovider.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.naming.ConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Represents a non-parsed ("raw") config-as-code.
 */


@Getter
public class ConfigResourceImpl implements ConfigResource{
    private String content;
    private ResourceType type;
    private File file;
    private URL url;
    private String  name;
    private Config config;

    public ConfigResourceImpl() {    
    }

    public void parse(ResourceType type, String filepath) throws ConfigurationException {
        file = new File(filepath);
        if(!file.exists()){
            throw new ConfigurationException("File not found: " + filepath);
        }
        name  = file.getName();
        this.type = type;
        parse(file);
    }
    
    public void parse(ResourceType type, URL url) throws ConfigurationException {
        this.type = type;
        this.url = url;
        parse(url);
    }
 
    public void parse(ResourceType type, String fileContent, String name) throws ConfigurationException {
        this.type = type;
        this.content = fileContent;
        this.name = name;
        parse(type, fileContent);
    }
    
    boolean isContentBased(){
        return StringUtils.isNotEmpty(content);
    }

    boolean isFileBased(){
        return file != null;
    }

    boolean isUrlBased(){
        return url != null;
    }

    private void parse(URL url) throws ConfigurationException {
        if(ResourceType.YML.equals(type)){
            config = yamlToConfig(url);
        }else if(ResourceType.JSON.equals(type)){
            config = jsonToConfig(url);
        }
    }
    
    private void parse(File file) throws ConfigurationException {
        if(ResourceType.YML.equals(type)){
            config = yamlToConfig(file);
        }else if(ResourceType.JSON.equals(type)){
            config = jsonToConfig(file);
        }
    }

    private void parse(String fileContent) throws ConfigurationException {
        if(ResourceType.YML.equals(type)){
            config = yamlToConfig(fileContent, "");
        }else if(ResourceType.JSON.equals(type)){
            config = jsonToConfig(fileContent);
        }
    }

    private Config jsonToConfig(String fileContent) {
        return ConfigFactory.parseString(fileContent);
    }

    private Config yamlToConfig(String yamlContent, String path) throws ConfigurationException {
        try{

            ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());

            Object obj = yamlReader.readValue(yamlContent, Object.class);
            ObjectMapper jsonWriter = new ObjectMapper();
            String jsonAsStr = jsonWriter.writeValueAsString(obj);
            return ConfigFactory.parseString(jsonAsStr);

        } catch (JsonProcessingException e) {
            throw new ConfigurationException("Unable to parse YML configuration file " + path);
        }
    }


    private Config jsonToConfig(File file) {
        return ConfigFactory.parseFile(file);
    }


    private Config jsonToConfig(URL file) {
        return ConfigFactory.parseURL(file);
    }
    
    public Config yamlToConfig(URL url) throws ConfigurationException {
        throw new UnsupportedOperationException();
    }

    public Config yamlToConfig(File file) throws ConfigurationException {

        try {
            String yamlContent = IOUtils.toString(new FileInputStream(file.getPath()), "UTF-8");

            return yamlToConfig(yamlContent, file.getPath()) ;

        } catch (IOException e) {
            throw new ConfigurationException("Unable to read URL " + file.getPath());

        }

        
    }
}
