package com.checkmarx.configprovider.apis;

import com.checkmarx.configprovider.ConfigProvider;
import com.checkmarx.configprovider.dto.SourceProviderType;
import com.checkmarx.configprovider.readers.EnvPropertiesReader;
import com.checkmarx.configprovider.readers.FileReader;
import com.checkmarx.configprovider.readers.ListReaders;
import com.checkmarx.configprovider.readers.PropertiesReader;
import com.checkmarx.configprovider.readers.RepoReader;
import com.checkmarx.configprovider.dto.ResourceType;
import com.checkmarx.configprovider.utility.PropertyLoader;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

import org.junit.Assert;

import javax.naming.ConfigurationException;

import java.io.FileNotFoundException;

import static org.junit.Assert.*;

@Slf4j
public class ConfigProviderAPIsTestSteps {
    
    private static final String GITHUB_REPO = "configProviderTests";
    private static final String GITHUB_NAMESPACE = "cxflowtestuser";

    private static final String GITHUB_TOKEN = "github.token";
    private static final String GITHUB_API_URL = "https://api.github.com";
    private static final String APPLICATION_TEST_API_YML = "application-test-api.yml";
    private static final String APPLICATION_TEST_API_JSON = "application-test-api.json";
    private static final String APPLICATION_SECRETS_TEST_API_YML = "application-test-api-secrets.yml";
    private static final String APP_NAME = "ConfigProviderAPIsTest";
    private static final String FLOW_1 = "flow1";
    private static final String ENV_PROP_GIT_HUB_TOKEN = "envPropGitHubToken";

    // private static final String GITHUB_CONFIG_AS_CODE = "github.configAsCode";
    // private static final String AST_PRESET = "ast.preset";
    // private static final String AST_TOKEN = "ast.token";

    // private static final String PRESET_FROM_GITHUB_YML = "presetYmlB";
    // private static final String JIRA_PROJECT = "jira.project";
    private static final String CONFIG_AS_CODE_FILE_NAME = "cx.configuration";


    static PropertyLoader props = new PropertyLoader();
    private static SourceProviderType providerType;

    private Exception exception;
    private ConfigProvider configProvider;
    private AstConfigurationLoaderTestClass astConfigurationLoaderTestClass;


    @Before()
    public void init(){
        configProvider = ConfigProvider.getInstance();
    }
    
    @After()
    public void clean(){
        configProvider.clear();
    }
    
    // @Given("env variable overrides application.yml property")
    // public void loadAppYmlAndThenEnvVars(){
    //     try {
    //         loadAppYml();
    //         loadEnvProperties(false);
            
    //     } catch (FileNotFoundException | ConfigurationException e) {
    //         Assert.fail(e.getMessage());
    //     }
    // }
    
    // @Given("Config provider loads all environment variables and then data from application.yml")
    // public void loadAppYmlAndThenAllEnvVars(){
    //     try {
    //         loadAppYml();
    //         loadEnvProperties(true);
            
    //     } catch (FileNotFoundException | ConfigurationException e) {
    //         Assert.fail(e.getMessage());
    //     }
    // }

    private PropertiesReader loadPropertiesRealToken() throws ConfigurationException {
        PropertiesReader envPropResourceImpl = new PropertiesReader();
        
        envPropResourceImpl.addPropertyPathValue(GITHUB_TOKEN, props.getProperty(GITHUB_TOKEN));
        
        return envPropResourceImpl;
    }
    
    // private ConfigObject loadEnvProperties(boolean loadAll) throws ConfigurationException {
    //     EnvPropertiesReader envPropResourceImpl = new EnvPropertiesReader(loadAll);
    //     envPropResourceImpl.addPropertyPathValue(GITHUB_TOKEN, ENV_PROP_GIT_HUB_TOKEN);
    //     configProvider.init(FLOW_1, envPropResourceImpl);
    //     return configProvider.getConfigObject(FLOW_1);
    // }

    @Given("application.yml properties overrides env variables")
    public void loadAppEnvVarsAndThenApplicationYml(){
        try {
            String filePath = props.getFileUrlInClassloader(APPLICATION_TEST_API_YML);
            EnvPropertiesReader envPropResourceImpl = new EnvPropertiesReader(false);
            envPropResourceImpl.addPropertyPathValue(GITHUB_TOKEN, ENV_PROP_GIT_HUB_TOKEN);
            FileReader fileResource = new FileReader(ResourceType.YAML,filePath);
            ListReaders multiple = new ListReaders();
            multiple.add(envPropResourceImpl);
            multiple.add(fileResource);
            configProvider.init(FLOW_1, multiple);
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
            PropertiesReader propertiesReader = loadPropertiesRealToken();
            loadGithubResources(propertiesReader, branch);

        } catch (FileNotFoundException | ConfigurationException e) {
            Assert.fail(e.getMessage());
        }
    }
    
    private void loadGithubResources(PropertiesReader baseResource, String branch) throws ConfigurationException {
        String token = props.getProperty(GITHUB_TOKEN);
        RepoReader repoResource = getRemoteRepoLocation(branch, token);
        repoResource.setConfigAsCodeFileName(CONFIG_AS_CODE_FILE_NAME);
        ListReaders listReaders = new ListReaders(baseResource).add(repoResource); 
        configProvider.init(FLOW_1, listReaders);
    }

    private void loadAppYml() throws FileNotFoundException, ConfigurationException {
        String filePath = props.getFileUrlInClassloader(APPLICATION_TEST_API_YML);
        FileReader fileResource = new FileReader(ResourceType.YAML, filePath);
        
        configProvider.init(APP_NAME, fileResource);
    }

    private void loadAppJson() throws FileNotFoundException, ConfigurationException {
        String filePath = props.getFileUrlInClassloader(APPLICATION_TEST_API_JSON);
        FileReader fileResource = new FileReader(ResourceType.JSON, filePath);
        
        configProvider.init(APP_NAME, fileResource);
    }

    @Given ("application.yml, env variables and application-secrets.yml are loaded into initial resource using MultipleResourcesImpl")
    public void testBaseResourceUsingMultipleResources() throws FileNotFoundException, ConfigurationException{
        String appYmlPath = props.getFileUrlInClassloader(APPLICATION_TEST_API_YML);
        String appSecretsYmlPath = props.getFileUrlInClassloader(APPLICATION_SECRETS_TEST_API_YML);
        FileReader appYmlResource = new FileReader(ResourceType.YAML, appYmlPath);
        FileReader appSecretsYmlResource = new FileReader(ResourceType.YAML, appSecretsYmlPath);
        EnvPropertiesReader envPropResourceImpl = new EnvPropertiesReader(false);
        envPropResourceImpl.addPropertyPathValue(GITHUB_TOKEN, ENV_PROP_GIT_HUB_TOKEN);

        ListReaders listReaders = new ListReaders();
        listReaders.add(appYmlResource);
        listReaders.add(appSecretsYmlResource);
        listReaders.add(envPropResourceImpl);
        
        configProvider.init(FLOW_1, listReaders);
    }

    @And ("the order of override will be based on the order of the files added to the MultipleResourcesImpl")
    public void doNothing(){}

    // @And ("AST token {string} and AST preset {string} will be taken from application-secrets.yml")
    // public void validateBaseTruncationOrder(String astTokenValue , String valuePreset){
    //     //value from secrets
    //     String valuePresetActual = configProvider.getConfigObject(FLOW_1).toConfig().getString(AST_PRESET);
    //     assertEquals(valuePreset, valuePresetActual);

    //     //value from secrets
    //     String valueTokenActual = configProvider.getConfigObject(FLOW_1).toConfig().getString(AST_TOKEN);
    //     assertEquals(astTokenValue, valueTokenActual);

    //     //value from application.yml
    //     String valueIncremental = configProvider.getConfigObject(FLOW_1).toConfig().getString("ast.incremental");
    //     assertEquals("false", valueIncremental);
    // }
    

    // @Then("GITHUB token from env variables is overridden by the {string} from application.yml")
    // @Then("GITHUB token from application-test-api.yml is overridden by {string} loaded from the env variables")
    // public void validateGithubToken(String resultToken){
        
    //     String token = configProvider.getConfigObject(FLOW_1).toConfig().getString(GITHUB_TOKEN);

    //     assertEquals(resultToken, token);
    // }
    
    // @Then("{string} env variable will override the one from application-test-api.yml")
    // public void varifyEnvVariables(String pathEnvVar){
        
    //     Assert.assertTrue(configProvider.getConfigObject(FLOW_1).keySet().contains(pathEnvVar));

    //     String path = configProvider.getConfigObject(FLOW_1).toConfig().getString(pathEnvVar);

    //     Assert.assertNotEquals(path, "appPath");
        
    // }


//    private String extractGithubTokenFromBaseResource(String appName) {
//        return configProvider.getConfigObject(appName).toConfig().getString(GITHUB_TOKEN);
//    }

    // @Then("AST preset from application.yml is overridden by the preset from config-as-code {string}")
    // @Then("AST preset from application.yml and preset from config-as-code is overridden by the preset from config-as-code.yml {string}")
    // @And("AST preset from application.yml and preset from config-as-code is overridden by the preset from b.yml {string}")
    // public void validatePresetFromConfigAsCode(String presetResult){
    //     String value = configProvider.getConfigObject(FLOW_1).toConfig().getString(AST_PRESET);
    //     assertEquals(presetResult, value);
    // }

    // @And("mutual parameter jira.project from config-as-code and from a.yml will be overridden by the {string} from b.yml")
    // public void validateMutualParamsFromGithubYml(String expectedJiraProjectValue){
    //     String valuePreset = configProvider.getConfigObject(FLOW_1).toConfig().getString(AST_PRESET);
    //     assertEquals(PRESET_FROM_GITHUB_YML, valuePreset);

    //     String valueJiraProject = configProvider.getConfigObject(FLOW_1).toConfig().getString(JIRA_PROJECT);
    //     assertEquals(expectedJiraProjectValue, valueJiraProject);

    // }

    // @Then("unique elements form all configuration file will exist in in the final configuration")
    //  public void validateUniqueFromGithubYml(){
    //     String valueUniqueFromA= configProvider.getConfigObject(FLOW_1).toConfig().getString("jira.issue-type");
    //     assertEquals( "valueUniqueFromA", valueUniqueFromA);

    //     String valueUniqueFromB = configProvider.getConfigObject(FLOW_1).toConfig().getString("jira.unique-field");
    //     assertEquals("valueFromB", valueUniqueFromB);
    // }
    
    @Given("a {word} configuration")
    public void loadConfiguration(String type) throws FileNotFoundException, ConfigurationException {
        switch (type) {
        case "YAML":
            loadAppYml();
            break;
        case "JSON":
            loadAppJson();
            break;
        }
    }

    @When("passing a new bean class")
    public void passingNewBeanClass() {
        astConfigurationLoaderTestClass = configProvider.getConfiguration(APP_NAME, "ast", AstConfigurationLoaderTestClass.class);
    }

    @Then("class values are initialized with configuration")
    public void validateReturnedClass() {
        log.info("validating a normal String");
        assertEquals("AST token value from configuration is not as expected", "astToken", astConfigurationLoaderTestClass.getToken());
        log.info("validating a String with special characters");
        assertEquals("AST API URI value from configuration is not as expected", "http://this.is.just.a.test", astConfigurationLoaderTestClass.getApiUrl());
        log.info("validating a resolved value");
        assertEquals("AST preset value from configuration is not as expected", System.getenv("JAVA_HOME"), astConfigurationLoaderTestClass.getPreset());
        log.info("validating a boolean value");
        assertFalse("AST incremental value from configuration is not as expected", astConfigurationLoaderTestClass.isIncremental());
        log.info("validating missing Optional parameter");
        assertFalse("AST myParam Optional parameter was true", astConfigurationLoaderTestClass.isMyParam());
    }

    private RepoReader getRemoteRepoLocation(String branch, String token) {
        
           return new RepoReader(GITHUB_API_URL,GITHUB_NAMESPACE,GITHUB_REPO, branch, token, SourceProviderType.GITHUB);
    }

}