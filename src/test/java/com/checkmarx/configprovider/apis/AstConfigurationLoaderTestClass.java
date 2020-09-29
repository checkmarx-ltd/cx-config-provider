package com.checkmarx.configprovider.apis;

import com.typesafe.config.Optional;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AstConfigurationLoaderTestClass {

    String apiUrl;
    String token;
    String preset;
    @Optional
    boolean myParam;
    boolean incremental;
}