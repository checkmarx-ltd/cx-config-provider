package com.checkmarx.configprovider.configoverriding;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "classpath:cucumber/configprovider/config-overriding.feature",
        tags = "not @Skip"
)
public class ConfigOverridingTest {
}
