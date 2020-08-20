package com.cx.configprovider.apis;

import com.cx.configprovider.ConfigProviderImpl;
import com.cx.configprovider.dto.RemoteRepo;
import com.cx.configprovider.dto.ResourceType;
import com.cx.configprovider.dto.SourceProviderType;
import com.cx.configprovider.resource.EnvPropResourceImpl;
import com.cx.configprovider.resource.FileResourceImpl;
import com.cx.configprovider.resource.RepoResourceImpl;
import com.cx.configprovider.utility.PropertyLoader;
import com.typesafe.config.ConfigObject;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.junit.Assert;

import javax.naming.ConfigurationException;

import java.io.FileNotFoundException;
import java.util.Arrays;

import static org.junit.Assert.*;


public class ConfigProviderAPIsTestSteps {

 
    
    private static final String GITHUB_REPO = "configProviderTests";
    private static final String GITHUB_NAMESPACE = "cxflowtestuser";
    private static final String BRANCH = "test2";
    private static final String GITHUB_TOKEN = "github.token";
    private static final String GITHUB_API_URL = "https://api.github.com";
    private static final String APPLICATION_TEST_API_YML = "application-test-api.yml";
    private static final String APP_NAME = "ConfigProviderAPIsTest";
    private static final String FLOW_1 = "flow1";
    private static final String ENV_PROP_GIT_HUB_TOKEN = "envPropGitHubToken";
    private static final String GITHUB_TOKEN_FROM_APP = "githubTokenFromApp";
    private static final String GITHUB_CONFIG_AS_CODE = "github.configAsCode";
    private static final String AST_PRESET = "ast.preset";
    private static final String PRESET_FROM_CONFIG_AS_CODE = "presetFromConfigAsCode";
    static PropertyLoader props = new PropertyLoader();
    private static SourceProviderType providerType;

    private Exception exception;
    private ConfigProviderImpl configProvider;


    @Before()
    public void init(){
        configProvider = new ConfigProviderImpl();
    }
    
    @After()
    public void clean(){
        configProvider.clear();
    }
    
    @Given("env variable truncates application.yml property")
    public void loadAppYmlAndThenEnvVars(){
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
    public void loadAppEnvVarsAndThenApplicationYml(){
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

    @Given("github config-as-code truncates application.yml")
    public void loadApplicationYmlAndThenGithubConfigAsCode(){
        try {
            
            String filePath = props.loadFileFromClassPath(APPLICATION_TEST_API_YML);
            FileResourceImpl fileResource = new FileResourceImpl(ResourceType.YML,filePath);
            configProvider.initBaseResource(APP_NAME, fileResource);
            
            EnvPropResourceImpl envPropResourceImpl = new EnvPropResourceImpl();
            envPropResourceImpl.addPropertyPathValue(GITHUB_TOKEN, ENV_PROP_GIT_HUB_TOKEN);
            ConfigObject configWithEnvs = configProvider.loadResource(FLOW_1, envPropResourceImpl);

            validateEnvPropGithubToken();
            
            String confgiAsCodeFile = configProvider.getStringValue(FLOW_1, GITHUB_CONFIG_AS_CODE);
            
            RemoteRepo repoLocation = getRemoteRepoLocation();
            RepoResourceImpl repoResource = new RepoResourceImpl(repoLocation);
            repoResource.setConfigAsCode(confgiAsCodeFile);
            configProvider.mergeResources(FLOW_1, repoResource, configWithEnvs);
            
        } catch (FileNotFoundException | ConfigurationException e) {
            Assert.fail(e.getMessage());
        }
    }
    
    @Then("GITHUB token from application-test-api.yml is truncated by those loaded from the env variables")
    public void validateEnvPropGithubToken(){
        String value = configProvider.getConfigObject(FLOW_1).toConfig().getString(GITHUB_TOKEN);
        assertEquals(ENV_PROP_GIT_HUB_TOKEN, value);
    }

    @Then("AST preset from application.yml is truncated by the preset from config-as-code")
    public void validatePresetFromConfigAsCode(){
        String value = configProvider.getConfigObject(FLOW_1).toConfig().getString(AST_PRESET);
        assertEquals(PRESET_FROM_CONFIG_AS_CODE, value);
    }

    @Then("GITHUB token from env variables is truncated by the one from application.yml")
    public void validateGithubTokenFromAppYml(){
        String value = configProvider.getConfigObject(FLOW_1).toConfig().getString(GITHUB_TOKEN);
        assertEquals(GITHUB_TOKEN_FROM_APP, value);
    }

    private static RemoteRepo getRemoteRepoLocation() {
           return RemoteRepo.builder()
                    .apiBaseUrl(GITHUB_API_URL)
                    .repoName(GITHUB_REPO)
                    .namespace(GITHUB_NAMESPACE)
                    .ref(BRANCH)
                    .accessToken(props.getProperty(GITHUB_TOKEN))
                    .sourceProviderType(SourceProviderType.GITHUB)
                    .build();
    }

}