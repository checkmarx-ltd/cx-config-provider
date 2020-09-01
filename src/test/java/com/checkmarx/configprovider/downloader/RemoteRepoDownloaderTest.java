package com.checkmarx.configprovider.downloader;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;



@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = { "pretty", "html:target/cucumber" },
        glue = { "com.checkmarx.configprovider.downloader" },
        features = "classpath:cucumber/configprovider/downloader.feature",
        tags = "not @Skip"
    )

public class RemoteRepoDownloaderTest {
}