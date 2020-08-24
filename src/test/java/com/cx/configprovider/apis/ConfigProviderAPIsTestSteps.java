package com.cx.configprovider.apis;

import com.cx.configprovider.ConfigProviderImpl;
import com.cx.configprovider.dto.RemoteRepo;
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
    private static final String BRANCH1 = "test1";
    private static final String BRANCH2 = "test2";
    private static final String BRANCH3 = "test3";
    private static final String GITHUB_TOKEN = "github.token";
    private static final String GITHUB_API_URL = "https://api.github.com";
    private static final String APPLICATION_TEST_API_YML = "application-test-api.yml";
    private static final String APPLICATION_SECRETS_TEST_API_YML = "application-test-api-secrets.yml";
    private static final String APP_NAME = "ConfigProviderAPIsTest";
    private static final String FLOW_1 = "flow1";
    private static final String ENV_PROP_GIT_HUB_TOKEN = "envPropGitHubToken";
    private static final String GITHUB_TOKEN_FROM_APP = "githubTokenFromApp";
    private static final String GITHUB_CONFIG_AS_CODE = "github.configAsCode";
    private static final String AST_PRESET = "ast.preset";
    private static final String AST_TOKEN = "ast.token";
    private static final String PRESET_FROM_CONFIG_AS_CODE = "presetFromConfigAsCode";
    private static final String PRESET_FROM_GITHUB_YML = "presetYmlB";
    private static final String JIRA_PROJECT = "jira.project";
    private static final String JIRA_PROJECT_FROM_GITHUB_YML = "jiraProjectB";
    private static final String PRESET_FROM_SECRETS_YML = "presetFromAppSecretsYml";

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
            loadAppYml();
            loadEnVProperties();
            
        } catch (FileNotFoundException | ConfigurationException e) {
            Assert.fail(e.getMessage());
        }
    }
    

    private ConfigObject loadPropertiesRealToken() throws ConfigurationException {
        PropResourceImpl envPropResourceImpl = new PropResourceImpl();
        
        envPropResourceImpl.addPropertyPathValue(GITHUB_TOKEN, props.getProperty(GITHUB_TOKEN));
        ConfigObject resource = configProvider.loadResource(FLOW_1, envPropResourceImpl);
        return resource;
    }
    
    private ConfigObject loadEnVProperties() throws ConfigurationException {
        EnvPropResourceImpl envPropResourceImpl = new EnvPropResourceImpl();
        envPropResourceImpl.addPropertyPathValue(GITHUB_TOKEN, ENV_PROP_GIT_HUB_TOKEN);
        ConfigObject resource = configProvider.loadResource(FLOW_1, envPropResourceImpl);
        return resource;
    }

    @Given("application.yml properties truncate env variables")
    public void loadAppEnvVarsAndThenApplicationYml(){
        try {
            String filePath = props.getFileUrlInClassloader(APPLICATION_TEST_API_YML);
            EnvPropResourceImpl envPropResourceImpl = new EnvPropResourceImpl();
            envPropResourceImpl.addPropertyPathValue(GITHUB_TOKEN, ENV_PROP_GIT_HUB_TOKEN);
            configProvider.initBaseResource(APP_NAME, envPropResourceImpl);
            FileResourceImpl fileResource = new FileResourceImpl(ResourceType.YML,filePath);
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
        RemoteRepo repoLocation = getRemoteRepoLocation(branch, token);
        RepoResourceImpl repoResource = new RepoResourceImpl(repoLocation);
        repoResource.setConfigAsCode(confgiAsCodeFile);
        configProvider.mergeResources(FLOW_1, repoResource, configWithEnvs);
    }

    private void loadAppYml() throws FileNotFoundException, ConfigurationException {
        String filePath = props.getFileUrlInClassloader(APPLICATION_TEST_API_YML);
        FileResourceImpl fileResource = new FileResourceImpl(ResourceType.YML, filePath);
        configProvider.initBaseResource(APP_NAME, fileResource);
    }

    @Given ("application.yml, env variables and application-secrets.yml are loaded into initial resource using MultipleResourcesImpl")
    public void testBaseResourceUsingMultipleResources() throws FileNotFoundException, ConfigurationException{
        String appYml = props.getFileUrlInClassloader(APPLICATION_TEST_API_YML);
        String appSecretsYml = props.getFileUrlInClassloader(APPLICATION_SECRETS_TEST_API_YML);
        FileResourceImpl appYmlResource = new FileResourceImpl(ResourceType.YML, appYml);
        FileResourceImpl appSecretsYmlResource = new FileResourceImpl(ResourceType.YML, appSecretsYml);
        EnvPropResourceImpl envPropResourceImpl = new EnvPropResourceImpl();
        envPropResourceImpl.addPropertyPathValue(GITHUB_TOKEN, ENV_PROP_GIT_HUB_TOKEN);

        MultipleResourcesImpl multipleResources = new MultipleResourcesImpl();
        multipleResources.add(appYmlResource);
        multipleResources.add(appSecretsYmlResource);
        multipleResources.add(envPropResourceImpl);
        
        configProvider.initBaseResource(FLOW_1, multipleResources);
    }

    @And ("the order of truncation will be based on the order of the files added to the MultipleResourcesImpl")
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
    

    @Then("GITHUB token from env variables is truncated by the {string} from application.yml")
    @Then("GITHUB token from application-test-api.yml is truncated by {string} loaded from the env variables")
    public void validateGithubToken(String resultToken){
        String value = extractGithubTokenFromBaseResource(FLOW_1);
        assertEquals(resultToken, value);
    }

    private String extractGithubTokenFromBaseResource(String appName) {
        return configProvider.getConfigObject(appName).toConfig().getString(GITHUB_TOKEN);
    }

    @Then("AST preset from application.yml is truncated by the preset from config-as-code {string}")
    @Then("AST preset from application.yml and preset from config-as-code is truncated by the preset from config-as-code.yml {string}")
    @And("AST preset from application.yml and preset from config-as-code is truncated by the preset from b.yml {string}")
    public void validatePresetFromConfigAsCode(String presetResult){
        String value = configProvider.getConfigObject(FLOW_1).toConfig().getString(AST_PRESET);
        assertEquals(presetResult, value);
    }

    @And("mutual parameter jira.project from config-as-code and from a.yml will be truncated by the {string} from b.yml")
    public void validateMutualParamsFromGithubYml(String expectedJiraProjectValue){
        String valuePreset = configProvider.getConfigObject(FLOW_1).toConfig().getString(AST_PRESET);
        assertEquals(PRESET_FROM_GITHUB_YML, valuePreset);

        String valueJiraProject = configProvider.getConfigObject(FLOW_1).toConfig().getString(JIRA_PROJECT);
        assertEquals(expectedJiraProjectValue, valueJiraProject);

    }

    @Then("unique elements form all configuration file will exist in in the final configuration")
     public void validateUniqueFromGithubYml(){
        String valueUniqueFromA= configProvider.getConfigObject(FLOW_1).toConfig().getString("jira.issue-type");
        assertEquals(valueUniqueFromA, "valueUniqueFromA");

        String valueUniqueFromB = configProvider.getConfigObject(FLOW_1).toConfig().getString("jira.unique-field");
        assertEquals(valueUniqueFromB, "valueFromB");
    }
    


    private static RemoteRepo getRemoteRepoLocation(String branch, String token) {
           return RemoteRepo.builder()
                    .apiBaseUrl(GITHUB_API_URL)
                    .repoName(GITHUB_REPO)
                    .namespace(GITHUB_NAMESPACE)
                    .ref(branch)
                    .accessToken(token)
                    .sourceProviderType(SourceProviderType.GITHUB)
                    .build();
    }

}