package com.cx.configprovider.downloader;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;



@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = { "pretty", "html:target/cucumber" },
        glue = { "com.cx.configprovider.downloader" },
        features = "classpath:cucumber/configprovider/downloader.feature",
        tags = "not @Skip"
    )

public class RemoteRepoDownloaderTest {
}