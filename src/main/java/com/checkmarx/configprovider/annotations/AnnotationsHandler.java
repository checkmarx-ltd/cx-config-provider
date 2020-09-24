package com.checkmarx.configprovider.annotations;

import java.util.Arrays;
import java.util.Optional;

import com.typesafe.config.Config;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AnnotationsHandler {
    
    private AnnotationsHandler() {}

    public static Optional<String> getCofigurationTreePath(Class<?> clazz) {
        return Optional.ofNullable(clazz.getAnnotation(ConfigurableClass.class))
        .map(ConfigurableClass::value);
    }

	public static <T extends Object> void setValuesByConfiguration(T object, Config currentConfig) {
        Arrays.stream(object.getClass().getDeclaredFields())
        .forEach(field -> 
            Optional.ofNullable(field.getAnnotation(ConfigurationProperty.class))
            .map(ConfigurationProperty::value).map(value -> value.isBlank() ? field.getName() : value)
            .ifPresent(name -> 
                Optional.ofNullable(currentConfig.getString(name)).filter(value -> !value.isEmpty())
                .ifPresent(value -> {
                    try {
                        boolean accessible = field.canAccess(object);
                        field.setAccessible(true);
                        field.set(object, value);
                        field.setAccessible(accessible);
                    } catch (Exception e) {
                        log.error("failed to update field");
                    }
                })
            )
        );
    }


}
