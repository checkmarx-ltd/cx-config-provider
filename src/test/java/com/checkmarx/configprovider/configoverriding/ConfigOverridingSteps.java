package com.checkmarx.configprovider.configoverriding;

import com.checkmarx.configprovider.ConfigProvider;
import com.checkmarx.configprovider.RemoteRepoDownloader;
import com.checkmarx.configprovider.dto.RepoDto;
import com.checkmarx.configprovider.dto.ResourceType;
import com.checkmarx.configprovider.resource.FileContentResource;
import com.checkmarx.configprovider.resource.FileResource;
import com.checkmarx.configprovider.resource.MultipleResources;
import com.checkmarx.configprovider.resource.ParsableResource;
import com.checkmarx.configprovider.resource.RepoResource;
import com.checkmarx.configprovider.utility.PropertyLoader;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import javax.naming.ConfigurationException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ConfigOverridingSteps {
    private static final PropertyLoader props = new PropertyLoader();
    private static final String TEST_UID = "ba92nakJrq";
    private static final String MISSING_PROPERTY_INDICATOR = "<missing>";
    private static final String NULL_VALUE_INDICATOR = "<null>";

    private final ConfigProvider configProvider = ConfigProvider.getInstance();
    private final List<ParsableResource> baseResources = new ArrayList<>();
    private String configAsCodeContents;

    @Given("default configuration contains {string} set to {string} value")
    public void defaultConfigurationContains(String propName, String propValue) {
        if (!propValue.equals(MISSING_PROPERTY_INDICATOR)) {
            if (propValue.equals(NULL_VALUE_INDICATOR)) {
                propValue = null;
            } else {
                propValue = StringUtils.wrapIfMissing(propValue, '"');
            }
            // A superset of JSON is actually used that allows dot notation.
            // It also allows null values, unlike the Java built-in properties format.
            FileContentResource configForStep = new FileContentResource(ResourceType.JSON,
                    String.format("%s: %s", propName, propValue),
                    "test.json");
            baseResources.add(configForStep);
        }
    }

    @When("initializing ConfigProvider with the default configuration")
    public void initializingConfigProvider() {
        MultipleResources resources = new MultipleResources(baseResources);
        configProvider.initBaseConfig(resources);
    }

    @And("config-as-code YAML file in the GitHub repository contains {string} with the {string} value")
    public void configAsCodeYAMLFile(String propName, String propValue) {
        StringBuilder yamlBuilder = new StringBuilder();
        if (!propValue.equals(MISSING_PROPERTY_INDICATOR)) {
            if (propValue.equals(NULL_VALUE_INDICATOR)) {
                propValue = "null";
            }

            final String INDENT = "  ";
            String[] parts = StringUtils.split(propName, '.');
            for (int i = 0; i < parts.length; i++) {
                boolean isLast = i == parts.length - 1;
                yamlBuilder.append(String.format("%s%s: %s%n", StringUtils.repeat(INDENT, i), parts[i], isLast ? propValue : ""));
            }
            configAsCodeContents = yamlBuilder.append(System.lineSeparator()).toString();
        } else {
            configAsCodeContents = "{}";
        }
    }

    @And("using ConfigProvider to load config-as-code")
    public void usingConfigProviderToLoadConfigAsCode() {
        FileContentResource yamlContent = new FileContentResource(ResourceType.YML, configAsCodeContents, "test.yaml");

        RemoteRepoDownloader downloader = mock(RemoteRepoDownloader.class);
        when(downloader.downloadRepoFiles(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        when(downloader.downloadRepoFiles(any(), any(), any(), eq("yml")))
                .thenReturn(Collections.singletonList(yamlContent));

        RepoDto repoInfo = RepoDto.builder().build();
        RepoResource repoResource = new RepoResource(repoInfo, downloader);

        configProvider.initConfig(TEST_UID, new MultipleResources(repoResource));
    }

    @Then("the resulting config will have the {string} set to the {string} value")
    public void theResultingConfigWillHave(String propPath, String expectedPropValue) {
        if (expectedPropValue.equals(NULL_VALUE_INDICATOR)) {
            expectedPropValue = null;
        }

        String actualPropValue = configProvider.getStringValue(TEST_UID, propPath);

        String message = String.format("Unexpected resulting value for the %s property", propPath);
        Assert.assertEquals(message, expectedPropValue, actualPropValue);
    }

    @Given("default configuration doesn't contain properties defined in the examples")
    public void defaultConfigurationDoesnTContainPropertiesDefinedInTheExamples() {
    }

    @And("the {string} environment variable has the {string} value")
    public void theEnvironmentVariableHasTheValue(String arg0, String arg1) {
    }
}
