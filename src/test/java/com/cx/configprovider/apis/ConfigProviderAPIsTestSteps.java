package com.cx.configprovider.apis;

import com.cx.configprovider.ConfigProvider;
import com.cx.configprovider.dto.ResourceType;
import com.cx.configprovider.dto.SourceProviderType;
import com.cx.configprovider.resource.*;
import com.cx.configprovider.utility.PropertyLoader;
import com.typesafe.config.ConfigObject;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.junit.Assert;

import javax.naming.ConfigurationException;

import java.io.FileNotFoundException;

import static org.junit.Assert.*;


public class ConfigProviderAPIsTestSteps {
    
    private static final String GITHUB_REPO = "configProviderTests";
    private static final String GITHUB_NAMESPACE = "cxflowtestuser";

    private static final String GITHUB_TOKEN = "github.token";
    private static final String GITHUB_API_URL = "https://api.github.com";
    private static final String APPLICATION_TEST_API_YML = "application-test-api.yml";
    private static final String APPLICATION_SECRETS_TEST_API_YML = "application-test-api-secrets.yml";
    private static final String APP_NAME = "ConfigProviderAPIsTest";
    private static final String FLOW_1 = "flow1";
    private static final String ENV_PROP_GIT_HUB_TOKEN = "envPropGitHubToken";

    private static final String GITHUB_CONFIG_AS_CODE = "github.configAsCode";
    private static final String AST_PRESET = "ast.preset";
    private static final String AST_TOKEN = "ast.token";

    private static final String PRESET_FROM_GITHUB_YML = "presetYmlB";
    private static final String JIRA_PROJECT = "jira.project";


    static PropertyLoader props = new PropertyLoader();
    private static SourceProviderType providerType;

    private Exception exception;
    private ConfigProvider configProvider;


    @Before()
    public void init(){
        configProvider = ConfigProvider.getInstance();
    }
    
    @After()
    public void clean(){
        configProvider.clear();
    }
    
    @Given("env variable overrides application.yml property")
    public void loadAppYmlAndThenEnvVars(){
        try {
            loadAppYml();
            loadEnvProperties(false);
            
        } catch (FileNotFoundException | ConfigurationException e) {
            Assert.fail(e.getMessage());
        }
    }
    
    @Given("Config provider loads all environment variables and then data from application.yml")
    public void loadAppYmlAndThenAllEnvVars(){
        try {
            loadAppYml();
            loadEnvProperties(true);
            
        } catch (FileNotFoundException | ConfigurationException e) {
            Assert.fail(e.getMessage());
        }
    }

    private ConfigObject loadPropertiesRealToken() throws ConfigurationException {
        PropertiesResource envPropResourceImpl = new PropertiesResource();
        
        envPropResourceImpl.addPropertyPathValue(GITHUB_TOKEN, props.getProperty(GITHUB_TOKEN));
        ConfigObject resource = configProvider.loadResource(FLOW_1, envPropResourceImpl);
        return resource;
    }
    
    private ConfigObject loadEnvProperties(boolean loadAll) throws ConfigurationException {
        EnvProperties envPropResourceImpl = new EnvProperties(loadAll);
        envPropResourceImpl.addPropertyPathValue(GITHUB_TOKEN, ENV_PROP_GIT_HUB_TOKEN);
        ConfigObject resource = configProvider.loadResource(FLOW_1, envPropResourceImpl);
        return resource;
    }

    @Given("application.yml properties overrides env variables")
    public void loadAppEnvVarsAndThenApplicationYml(){
        try {
            String filePath = props.getFileUrlInClassloader(APPLICATION_TEST_API_YML);
            EnvProperties envPropResourceImpl = new EnvProperties(false);
            envPropResourceImpl.addPropertyPathValue(GITHUB_TOKEN, ENV_PROP_GIT_HUB_TOKEN);
            configProvider.initBaseResource(APP_NAME, envPropResourceImpl);
            FileResource fileResource = new FileResource(ResourceType.YML,filePath);
            configProvider.loadResource(FLOW_1, fileResource);
        } catch (FileNotFoundException | ConfigurationException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Given("github config-as-code\\(GITHUB) over env variables over application.yml in branch {string}")
    @Given("config-as-code.yml over config-as-code\\(GITHUB) over env variables over application.yml in github branch {string}")
    @Given("in github branch {string}: b.yml\\(GITHUB) over a.yml\\(GITHUB) over config-as-code\\(GITHUB) over env variables over application.yml")
    public void loadApplicationYmlAndThenGithubConfigAsCode(String branch){
        try {

            loadAppYml();
            ConfigObject configWithEnvs = loadPropertiesRealToken();
            loadGithubResources(configWithEnvs, branch);

        } catch (FileNotFoundException | ConfigurationException e) {
            Assert.fail(e.getMessage());
        }
    }
    
    private void loadGithubResources(ConfigObject configWithEnvs, String branch) throws ConfigurationException {
        String confgiAsCodeFile = configProvider.getStringValue(FLOW_1, GITHUB_CONFIG_AS_CODE);
        String token = extractGithubTokenFromBaseResource(FLOW_1);
        RepoResource repoResource = getRemoteRepoLocation(branch, token);
        repoResource.setConfigAsCodeFileName(confgiAsCodeFile);
        configProvider.loadResources(FLOW_1, repoResource, configWithEnvs);
    }

    private void loadAppYml() throws FileNotFoundException, ConfigurationException {
        String filePath = props.getFileUrlInClassloader(APPLICATION_TEST_API_YML);
        FileResource fileResource = new FileResource(ResourceType.YML, filePath);
        configProvider.initBaseResource(APP_NAME, fileResource);
    }

    @Given ("application.yml, env variables and application-secrets.yml are loaded into initial resource using MultipleResourcesImpl")
    public void testBaseResourceUsingMultipleResources() throws FileNotFoundException, ConfigurationException{
        String appYmlPath = props.getFileUrlInClassloader(APPLICATION_TEST_API_YML);
        String appSecretsYmlPath = props.getFileUrlInClassloader(APPLICATION_SECRETS_TEST_API_YML);
        FileResource appYmlResource = new FileResource(ResourceType.YML, appYmlPath);
        FileResource appSecretsYmlResource = new FileResource(ResourceType.YML, appSecretsYmlPath);
        EnvProperties envPropResourceImpl = new EnvProperties(false);
        envPropResourceImpl.addPropertyPathValue(GITHUB_TOKEN, ENV_PROP_GIT_HUB_TOKEN);

        MultipleResources multipleResources = new MultipleResources();
        multipleResources.add(appYmlResource);
        multipleResources.add(appSecretsYmlResource);
        multipleResources.add(envPropResourceImpl);
        
        configProvider.initBaseResource(FLOW_1, multipleResources);
    }

    @And ("the order of override will be based on the order of the files added to the MultipleResourcesImpl")
    public void doNothing(){}

    @And ("AST token {string} and AST preset {string} will be taken from application-secrets.yml")
    public void validateBaseTruncationOrder(String astTokenValue , String valuePreset){
        //value from secrets
        String valuePresetActual = configProvider.getConfigObject(FLOW_1).toConfig().getString(AST_PRESET);
        assertEquals(valuePreset, valuePresetActual);

        //value from secrets
        String valueTokenActual = configProvider.getConfigObject(FLOW_1).toConfig().getString(AST_TOKEN);
        assertEquals(astTokenValue, valueTokenActual);

        //value from application.yml
        String valueIncremental = configProvider.getConfigObject(FLOW_1).toConfig().getString("ast.incremental");
        assertEquals("false", valueIncremental);
    }
    

    @Then("GITHUB token from env variables is overridden by the {string} from application.yml")
    @Then("GITHUB token from application-test-api.yml is overridden by {string} loaded from the env variables")
    public void validateGithubToken(String resultToken){
        String value = extractGithubTokenFromBaseResource(FLOW_1);
        assertEquals(resultToken, value);
    }
    
    @Then("{string} env variable will override the one from application-test-api.yml")
    public void varifyEnvVariables(String pathEnvVar){
        
        Assert.assertTrue(configProvider.getConfigObject(FLOW_1).keySet().contains(pathEnvVar));

        String path = configProvider.getConfigObject(FLOW_1).toConfig().getString(pathEnvVar);

        Assert.assertNotEquals(path, "appPath");
        
    }


    private String extractGithubTokenFromBaseResource(String appName) {
        return configProvider.getConfigObject(appName).toConfig().getString(GITHUB_TOKEN);
    }

    @Then("AST preset from application.yml is overridden by the preset from config-as-code {string}")
    @Then("AST preset from application.yml and preset from config-as-code is overridden by the preset from config-as-code.yml {string}")
    @And("AST preset from application.yml and preset from config-as-code is overridden by the preset from b.yml {string}")
    public void validatePresetFromConfigAsCode(String presetResult){
        String value = configProvider.getConfigObject(FLOW_1).toConfig().getString(AST_PRESET);
        assertEquals(presetResult, value);
    }

    @And("mutual parameter jira.project from config-as-code and from a.yml will be overridden by the {string} from b.yml")
    public void validateMutualParamsFromGithubYml(String expectedJiraProjectValue){
        String valuePreset = configProvider.getConfigObject(FLOW_1).toConfig().getString(AST_PRESET);
        assertEquals(PRESET_FROM_GITHUB_YML, valuePreset);

        String valueJiraProject = configProvider.getConfigObject(FLOW_1).toConfig().getString(JIRA_PROJECT);
        assertEquals(expectedJiraProjectValue, valueJiraProject);

    }

    @Then("unique elements form all configuration file will exist in in the final configuration")
     public void validateUniqueFromGithubYml(){
        String valueUniqueFromA= configProvider.getConfigObject(FLOW_1).toConfig().getString("jira.issue-type");
        assertEquals( "valueUniqueFromA", valueUniqueFromA);

        String valueUniqueFromB = configProvider.getConfigObject(FLOW_1).toConfig().getString("jira.unique-field");
        assertEquals("valueFromB", valueUniqueFromB);
    }
    


    private RepoResource getRemoteRepoLocation(String branch, String token) {
        
           return new RepoResource(GITHUB_API_URL,GITHUB_NAMESPACE,GITHUB_REPO, branch, token, SourceProviderType.GITHUB);
    }

}