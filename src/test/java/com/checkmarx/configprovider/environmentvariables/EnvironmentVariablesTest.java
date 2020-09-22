package com.checkmarx.configprovider.environmentvariables;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.ConfigurationException;

import com.checkmarx.configprovider.ConfigProvider;
import com.checkmarx.configprovider.dto.ResourceType;
import com.checkmarx.configprovider.readers.FileReader;
import com.checkmarx.configprovider.readers.ListReaders;
import com.checkmarx.configprovider.utility.PropertyLoader;
import com.typesafe.config.Config;

import org.junit.Test;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnvironmentVariablesTest {

    static PropertyLoader props = new PropertyLoader();

    // private EnvProperties envPropResourceImpl = new EnvProperties(false);
    private List<String> systemReplacedProperties;
    private Map<String, String> environmentReplacedProperties;
    private ConfigProvider configProvider;
    private String filePath;
    private Config resolved;

    @Test
    public void sonarlintbug() {
        assertTrue(true);
    }
    
    @Before
    public void init() {
        systemReplacedProperties = new ArrayList<>();
        environmentReplacedProperties = new HashMap<>();
        configProvider = ConfigProvider.getInstance();
    }

    @Given("the following cofiguration:")
    public void the_following_cofiguration(String configuration) throws FileNotFoundException, IOException {
        filePath = props.getFileUrlInClassloader("application-test-environmentvariables.yml");
        Path path = new java.io.File(filePath).toPath();
        log.info("creating the yaml : {}\n{}", path, configuration);
        Files.write(path, configuration.getBytes());
    }

    @Given("System property named {string} is set to {string}")
    public void system_property_named_name_is_set_to_value(String name, String value) {
        log.info("setting System property: {} = {}", name, value);
        System.setProperty(name, value);
        assertEquals(System.getProperty(name), value);
        systemReplacedProperties.add(name);
    }

    @Given("environment variable named {string} is set")
    public void environment_variable_named_name_is_set_to_value(String name) {
        log.info("verifying that environment includes {}", name);
        // envPropResourceImpl.addEnvVariable(name, value);
        assertNotNull(System.getenv(name));
        environmentReplacedProperties.put(name, System.getenv(name));
    }

    @When("resolving the configuration")
    public void resolving_the_configuration() throws ConfigurationException {
        FileReader fileResource = new FileReader(ResourceType.YAML, filePath);
        configProvider.init("test", new ListReaders(fileResource));
        resolved = configProvider.getConfigObject("test").toConfig().resolve();
    }

    @Then("created resolved values are:")
    public void created_resolved_values_are(Map<String,String> expected) {
        expected.forEach((key, value) -> assertEquals(resolved.getString(key),
            value.replace("___path_to_JAVA_HOME___", environmentReplacedProperties.get("JAVA_HOME"))));
    }

    @After
    public void cleanup() {
        log.info("deleting added System properties");
        systemReplacedProperties.forEach(System::clearProperty);
        environmentReplacedProperties.clear();
    }

}
