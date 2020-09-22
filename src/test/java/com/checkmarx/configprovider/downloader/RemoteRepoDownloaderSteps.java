package com.checkmarx.configprovider.downloader;

import com.checkmarx.configprovider.dto.SourceProviderType;


import com.checkmarx.configprovider.exceptions.ConfigProviderException;
import com.checkmarx.configprovider.readers.Processor;
import com.checkmarx.configprovider.readers.RepoReader;
import com.checkmarx.configprovider.utility.PropertyLoader;

import com.typesafe.config.Config;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.junit.Assert;

import javax.naming.ConfigurationException;

import java.util.Arrays;

import static org.junit.Assert.*;


public class RemoteRepoDownloaderSteps {

 
    
    private static final String GITHUB_REPO = "configProviderTests";
    private static final String GITHUB_NAMESPACE = "cxflowtestuser";
    private static final String BRANCH = "test1";
    private static final String GITHUB_TOKEN = "github.token";
    private static final String GITHUB_API_URL = "https://api.github.com";
    static PropertyLoader props = new PropertyLoader();
    private static SourceProviderType providerType;
    private static Config config;

    private Exception exception;
    

    @When("repository source is GITHUB")
    public void setRepositorySource(){
        providerType = SourceProviderType.GITHUB;
        exception = null;
    }

    
    @Then ("configuration provider will retrieve the configuration {string} from repository")
    public void setPath(String path){
        try {
            config = getConfigFromPath(path);
        } catch (Exception e) {
            exception = e;
        }
    }
    @And("the the returned configuration object will be {string}")
    public void checkOutput(String expected) {
        if(exception!=null){
            return;
        }
        else if(config == null){
            fail("config not populated");
        }
        else if(expected.equals("empty")){
            assertEmptyContent();
        }else{
            assertNonEmptyContent();
        }
    }
    
    @And("exception will be {string}")
    public void verifyException(String isException){
        if(Boolean.parseBoolean(isException)){
            assertEquals("Unexpected exception type.", ConfigProviderException.class, exception.getClass());
        }
    }

    private static void assertEmptyContent() {
        
        Assert.assertTrue("Expected Config-as-code file content to be empty.", config.root().render().isEmpty());
        
    }

    private static void assertNonEmptyContent() {
  
        assertTrue("Config-as-code file content is empty.", !config.root().render().isEmpty());
        
    }

    private Config getConfigFromPath(String path) throws ConfigurationException {

        RepoReader repoResource = getRemoteRepo();
        repoResource.setFoldersToSearch(Arrays.asList(path));
        
        config = Processor.load(repoResource);
        assertNotNull("Config-as-code object must always be non-null.", config);
        assertTrue("File content must always be non-null.", !config.isEmpty());
        return config;
    }

    private RepoReader getRemoteRepo() {
        if(SourceProviderType.GITHUB.equals(providerType)) {
            return new RepoReader(GITHUB_API_URL,GITHUB_NAMESPACE,GITHUB_REPO, BRANCH, props.getProperty(GITHUB_TOKEN), SourceProviderType.GITHUB);
        }else{
            throw new UnsupportedOperationException();
        }
    }

}