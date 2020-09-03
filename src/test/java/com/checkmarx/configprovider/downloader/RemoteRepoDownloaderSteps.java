package com.checkmarx.configprovider.downloader;

import com.checkmarx.configprovider.dto.SourceProviderType;
import com.checkmarx.configprovider.exceptions.ConfigProviderException;
import com.checkmarx.configprovider.resource.Parser;
import com.checkmarx.configprovider.resource.RepoResource;
import com.checkmarx.configprovider.utility.PropertyLoader;
import com.typesafe.config.Config;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import javax.naming.ConfigurationException;
import java.util.Arrays;
import java.util.Collections;

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
        if (exception == null) {
            if (config == null) {
                fail("config not populated");
            } else if (expected.equals("empty")) {
                assertEmptyContent();
            } else {
                assertNonEmptyContent();
            }
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
        assertFalse("Config-as-code file content is empty.", config.root().render().isEmpty());
    }

    private Config getConfigFromPath(String path) throws ConfigurationException {

        RepoResource repoResource = getRemoteRepo();
        repoResource.setFoldersToSearch(Collections.singletonList(path));
        
        config = Parser.parse(repoResource);
        assertNotNull("Config-as-code object must always be non-null.", config);
        assertFalse("File content must always be non-null.", config.isEmpty());
        return config;
    }

    private RepoResource getRemoteRepo() {
        if(SourceProviderType.GITHUB.equals(providerType)) {
            return new RepoResource(GITHUB_API_URL,GITHUB_NAMESPACE,GITHUB_REPO, BRANCH, props.getProperty(GITHUB_TOKEN), SourceProviderType.GITHUB);
        }else{
            throw new UnsupportedOperationException();
        }
    }
}