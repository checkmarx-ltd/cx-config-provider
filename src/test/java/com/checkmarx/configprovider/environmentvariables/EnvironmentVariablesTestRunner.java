package com.checkmarx.configprovider.environmentvariables;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = { "pretty", "html:target/cucumber" },
        glue = { "com.checkmarx.configprovider.environmentvariables" },
        features = "classpath:cucumber/configprovider/evironment-variables.feature",
        tags = "not @Skip"
    )
public class EnvironmentVariablesTestRunner {
    
}
