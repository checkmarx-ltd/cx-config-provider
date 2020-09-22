package com.checkmarx.configprovider.readers;

import com.checkmarx.configprovider.dto.interfaces.ConfigReader;
import com.typesafe.config.Config;

import javax.naming.ConfigurationException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class should be used with applying several Configuration Readers at the same time.
 * The elements will be applied based on the order ot their addition to the class,
 * unless there is a rule for the order of applying of specific element types
 */
public class ListReaders extends Parsable implements ConfigReader {

    List<ConfigReader> readersList = new LinkedList<>();

    public ListReaders(){}
    
    
    public ListReaders(ConfigReader configReader){
        add(configReader);
    }
    
    public ListReaders(List<ConfigReader> configReaders){
        addAll(configReaders);
    }

    /**
     * Concatenate elements to the end of the existing elements in the list
     * @param readers to add
     * @return
     */
    public ListReaders addAll(List<ConfigReader> readers){
        readersList.addAll(readers);
        applyOrder();
        return this;
    }

    /**
     * Concatenate elements to the end of the existing elements in the list
     * @param configReader to add
     * @return
     */
    public ListReaders add(ConfigReader configReader){
        readersList.add(configReader);
        applyOrder();
        return  this;
    }

    private void applyOrder() {
        //TO IMPLEMENT
        
        //Env Yaml
        //application.yml
        //config-as-code
    }

    /**
     * Converts a list on configuration files to a config
     * tree. The elements will be applied according to their order in the list.
     * If there are multiple parameter with the same name and path in the list, 
     * the value will be overridden by the those of parameters located farther 
     * in the list
     * @return Config tree
     * @throws ConfigurationException exception
     */
    @Override
    Config toConfig() throws ConfigurationException {
        Config current = null;
        for (ConfigReader configSource : readersList) {
            Config base = ((Parsable)configSource).toConfig();
            current = Optional.ofNullable(current)
                .map(base::withFallback)
                .orElse(base); /* for first iteration */
        }

        return current;
    }

    @Override
    public String getName() {
        return readersList.stream().map(reader -> ((ConfigReader)reader).getName().concat(" ")).collect(Collectors.toList()).toString();
    }

}
