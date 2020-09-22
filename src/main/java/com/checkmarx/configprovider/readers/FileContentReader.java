package com.checkmarx.configprovider.readers;

import com.checkmarx.configprovider.dto.interfaces.ConfigReader;
import com.checkmarx.configprovider.dto.ResourceType;
import com.typesafe.config.Config;
import lombok.Getter;

import javax.naming.ConfigurationException;
import java.util.Optional;

/**
 * Contains String content of resources of type Yml or Json
 */
@Getter
public class FileContentReader extends AbstractFileReader implements ConfigReader {
    private String content;
    private String  name;
    /**
    * @deprecated use {@link #FileContentReader(String, String, ResourceType)}
    */
    @Deprecated
    public FileContentReader(ResourceType type, String fileContent, String name)  {
        this(fileContent, name, type);
    }

    public FileContentReader(String fileContent, String name) {
        this(fileContent, name, ResourceType.getTypeByNameOrExtention(name));
    }

    public FileContentReader(String fileContent, String name, ResourceType type) {
        this.type = type;
        this.content = fileContent;
        this.name = name;
    }

    /**
     * Converts file String content of type Yml or Json to Config object.
     * Other types of files are not supported.
     * @return Config object
     * @throws ConfigurationException exception when the string content of the resource
     * can not be converted to Config object
     */
    @Override
    public Config toConfig() throws ConfigurationException {
        if(ResourceType.YAML.equals(type)){
            config = yamlToConfig(content, "");
        }else if(ResourceType.JSON.equals(type)){
            config = jsonToConfig(content);
        }
        
        return config;
    }

    @Override
    public String getName() {
        return Optional.ofNullable(name).orElse(type.name()) ;
    }


}
