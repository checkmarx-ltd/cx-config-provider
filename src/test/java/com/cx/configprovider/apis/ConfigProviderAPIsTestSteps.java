package com.cx.configprovider.apis;

import com.cx.configprovider.ConfigProviderImpl;
import com.cx.configprovider.RemoteRepoDownloader;
import com.cx.configprovider.dto.ConfigLocation;
import com.cx.configprovider.dto.RemoteRepoLocation;
import com.cx.configprovider.dto.ResourceType;
import com.cx.configprovider.dto.SourceProviderType;
import com.cx.configprovider.dto.interfaces.ConfigResource;
import com.cx.configprovider.exceptions.ConfigProviderException;
import com.cx.configprovider.interfaces.ConfigProvider;
import com.cx.configprovider.resource.EnvPropResourceImpl;
import com.cx.configprovider.resource.FileResourceImpl;
import com.cx.configprovider.utility.PropertyLoader;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import javax.naming.ConfigurationException;

import java.io.FileNotFoundException;

import static org.junit.Assert.*;


public class ConfigProviderAPIsTestSteps {

 
    
    private static final String GITHUB_REPO = "configProviderTests";
    private static final String GITHUB_NAMESPACE = "cxflowtestuser";
    private static final String BRANCH = "test1";
    private static final String GITHUB_TOKEN = "github.token";
    private static final String GITHUB_API_URL = "https://api.github.com";
    private static final String APPLICATION_TEST_API_YML = "application-test-api.yml";
    private static final String APP_NAME = "ConfigProviderAPIsTest";
    private static final String FLOW_1 = "flow1";
    private static final String ENV_PROP_GIT_HUB_TOKEN = "envPropGitHubToken";
    private static final String GITHUB_TOKEN_FROM_APP = "githubTokenFromApp";
    static PropertyLoader props = new PropertyLoader();
    private static SourceProviderType providerType;
    private ConfigResource config;
    private Exception exception;
    private ConfigProviderImpl configProvider;


    @Before
    public void init(){
        configProvider = new ConfigProviderImpl();
    }
    
    @Given("env variable truncates application.yml property")
    public void loadAppYmlFirst(){
        try {
            String filePath = props.loadFileFromClassPath(APPLICATION_TEST_API_YML);
            FileResourceImpl fileResource = new FileResourceImpl(ResourceType.YML,filePath);
            configProvider.initBaseResource(APP_NAME, fileResource);
            EnvPropResourceImpl envPropResourceImpl = new EnvPropResourceImpl();
            envPropResourceImpl.addPropertyPathValue(GITHUB_TOKEN, ENV_PROP_GIT_HUB_TOKEN);
            configProvider.loadResource(FLOW_1, envPropResourceImpl);
        } catch (FileNotFoundException | ConfigurationException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Given("application.yml properties truncate env variables")
    public void loadAppEnvVarsFirst(){
        try {
            String filePath = props.loadFileFromClassPath(APPLICATION_TEST_API_YML);
            EnvPropResourceImpl envPropResourceImpl = new EnvPropResourceImpl();
            envPropResourceImpl.addPropertyPathValue(GITHUB_TOKEN, ENV_PROP_GIT_HUB_TOKEN);
            configProvider.initBaseResource(APP_NAME, envPropResourceImpl);
            FileResourceImpl fileResource = new FileResourceImpl(ResourceType.YML,filePath);
            configProvider.loadResource(FLOW_1, fileResource);
        } catch (FileNotFoundException | ConfigurationException e) {
            Assert.fail(e.getMessage());
        }
    }
    
    @Then("GITHUB token from application-test-api.yml is truncated by those loaded from the env variables")
    public void loadConfigProvider(){
        String value = configProvider.getConfigObject(FLOW_1).toConfig().getString(GITHUB_TOKEN);
        assertEquals(ENV_PROP_GIT_HUB_TOKEN, value);
    }

    @Then("GITHUB token from env variables is truncated by the one from application.yml")
    public void loadConfigProvider2(){
        String value = configProvider.getConfigObject(FLOW_1).toConfig().getString(GITHUB_TOKEN);
        assertEquals(GITHUB_TOKEN_FROM_APP, value);
    }
//
//    @When("repository source is GITHUB")
//    public void setRepositorySource(){
//        providerType = SourceProviderType.GITHUB;
//        exception = null;
//    }
//
//    
//    @Then ("configuration provider will retrieve the configuration {string} from repository")
//    public void setPath(String path){
//        try {
//            config = getConfigFromPath(path);
//        } catch (Exception e) {
//            exception = e;
//        }
//    }
//    @And("the the returned configuration object will be {string}")
//    public void checkOutput(String expected) {
//        if(exception!=null){
//            return;
//        }
//        else if(config == null){
//            fail("config not populated");
//        }
//        else if(expected.equals("empty")){
//            assertEmptyContent(config);
//        }else{
//            assertNonEmptyContent(config);
//        }
//    }
//    
//    @And("exception will be {string}")
//    public void verifyException(String isException){
//        if(Boolean.parseBoolean(isException)){
//            assertEquals("Unexpected exception type.", ConfigProviderException.class, exception.getClass());
//        }
//    }
//
//    private static void assertEmptyContent(ConfigResource config) {
//        try {
//            Assert.assertTrue("Expected Config-as-code file content to be empty.", config.parse().isEmpty());
//        } catch (ConfigurationException e) {
//            Assert.fail(e.getMessage());
//        }
//    }
//
//    private static void assertNonEmptyContent(ConfigResource config) {
//        try {
//            assertTrue("Config-as-code file content is empty.", !config.parse().isEmpty());
//        } catch (ConfigurationException e) {
//            Assert.fail(e.getMessage());
//        }
//    }
//
//    private static ConfigResource getConfigFromPath(String path) throws ConfigurationException {
//
//        RemoteRepoLocation repoLocation = getRemoteRepoLocation();
//
//        ConfigLocation location = ConfigLocation.builder()
//                .path(path)
//                .repoLocation(repoLocation)
//                .build();
//
//        RemoteRepoDownloader downloader = new RemoteRepoDownloader();
//
//        ConfigResource result = downloader.getConfigAsCode(location);
//        assertNotNull("Config-as-code object must always be non-null.", result);
//        assertNotNull("File content must always be non-null.", result.parse().toString());
//        return result;
//    }
//
//    private static RemoteRepoLocation getRemoteRepoLocation() {
//        RemoteRepoLocation repoLocation;
//        if(SourceProviderType.GITHUB.equals(providerType)) {
//             repoLocation = RemoteRepoLocation.builder()
//                    .apiBaseUrl(GITHUB_API_URL)
//                    .repoName(GITHUB_REPO)
//                    .namespace(GITHUB_NAMESPACE)
//                    .ref(BRANCH)
//                    .accessToken(props.getProperty(GITHUB_TOKEN))
//                    .sourceProviderType(SourceProviderType.GITHUB)
//                    .build();
//        }else{
//            throw new UnsupportedOperationException();
//        }
//        return repoLocation;
//    }

}