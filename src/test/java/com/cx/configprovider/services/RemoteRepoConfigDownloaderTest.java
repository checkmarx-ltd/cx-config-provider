package com.cx.configprovider.services;

import com.cx.configprovider.dto.ConfigLocation;
import com.cx.configprovider.dto.RawConfigAsCode;
import com.cx.configprovider.dto.RemoteRepoLocation;
import com.cx.configprovider.dto.SourceProviderType;


import com.cx.configprovider.exceptions.ConfigProviderException;
import com.cx.utility.TestPropertyLoader;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.util.Properties;

import static org.junit.Assert.*;


public class RemoteRepoConfigDownloaderTest {
    static Properties props;
    private RawConfigAsCode config;
    private Exception exception;

    @Before()
    public static void loadProperties() {
        TestPropertyLoader propertyLoader = new TestPropertyLoader();
        props = propertyLoader.getProperties();
    }

    @When("given {string} is provided to configuration provider")
    public void setPath(String path){
        try {
            config = getConfigFromPath(path);
        } catch (Exception e) {
            exception = e;
         }
    }
    
    @Then("The the returned configuration object will be {string}")
    public void checkOutput(String expected) {
        if(config == null){
            fail("config not populated");
        }
        else if(expected.equals("empty")){
            assertEmptyContent(config);
        }else{
            assertNonEmptyContent(config);
        }
    }
    
    @And("exception will be {string}")
    public void verifyException(String isException){
        if(Boolean.parseBoolean(isException)){
            assertEquals("Unexpected exception type.", ConfigProviderException.class, exception.getClass());
        }
    }
//
//    @Test
//    public void getConfigAsCode_directoryWithSingleFile_hasContent() {
//        RawConfigAsCode config = getConfigFromPath(".checkmarx");
//        assertNonEmptyContent(config);
//    }
//
//    @Test
//    public void getConfigAsCode_deepDirectory_hasContent() {
//        RawConfigAsCode config = getConfigFromPath("deep/directory/structure");
//        assertNonEmptyContent(config);
//    }
//
//    @Test
//    public void getConfigAsCode_nonExistingPath_emptyContent() {
//        RawConfigAsCode config = getConfigFromPath("inexistence");
//        assertEmptyContent(config);
//    }
//
//    @Test
//    public void getConfigAsCode_fileInsteadOfDirectory_emptyContent() {
//        RawConfigAsCode config = getConfigFromPath(".checkmarx/config-as-code.yml");
//        assertEmptyContent(config);
//    }
//
//    @Test
//    public void getConfigAsCode_directoryWithoutFiles_emptyContent() {
//        RawConfigAsCode config = getConfigFromPath("deep/directory");
//        assertEmptyContent(config);
//    }
//
//    @Test
//    public void getConfigAsCode_emptyFile_emptyContent() {
//        RawConfigAsCode config = getConfigFromPath("directory-with-empty-file");
//        assertEmptyContent(config);
//    }
//
//    @Test
//    public void getConfigAsCode_directoryWithMultipleFiles_exception() {
//        try {
//            getConfigFromPath("config-as-code-test");
//            fail("Expected an exception to be thrown.");
//        } catch (Exception e) {
//   
//            assertEquals("Unexpected exception type.", ConfigProviderException.class, e.getClass());
//        }
//    }

    private static void assertEmptyContent(RawConfigAsCode config) {
        Assert.assertTrue("Expected Config-as-code file content to be empty.", config.getContent().isEmpty());
    }

    private static void assertNonEmptyContent(RawConfigAsCode config) {
        assertTrue("Config-as-code file content is empty.", StringUtils.isNotEmpty(config.getContent()));
    }

    private static RawConfigAsCode getConfigFromPath(String path) {
        RemoteRepoLocation repoLocation = RemoteRepoLocation.builder()
                .apiBaseUrl("https://api.github.com")
                .repoName("Cx-FlowRepo")
                .namespace("cxflowtestuser")
                .ref("master")
                .accessToken(props.getProperty("github.token"))
                .sourceProviderType(SourceProviderType.GITHUB)
                .build();

        ConfigLocation location = ConfigLocation.builder()
                .path(path)
                .repoLocation(repoLocation)
                .build();

        RemoteRepoConfigDownloader downloader = new RemoteRepoConfigDownloader();

        RawConfigAsCode result = downloader.getConfigAsCode(location);
        assertNotNull("Config-as-code object must always be non-null.", result);
        assertNotNull("File content must always be non-null.", result.getContent());

        return result;
    }
}