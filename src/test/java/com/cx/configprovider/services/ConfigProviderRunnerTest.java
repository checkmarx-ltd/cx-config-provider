package com.cx.configprovider.services;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;



@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = { "pretty", "html:target/cucumber" },
        glue = { "com.cx.configprovider.services" },
        features = "classpath:cucumber/configprovider/configProvider.feature",
        tags = "not @Skip"
    )

public class ConfigProviderRunnerTest {
}