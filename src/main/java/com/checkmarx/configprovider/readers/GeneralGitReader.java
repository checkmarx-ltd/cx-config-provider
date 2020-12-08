package com.checkmarx.configprovider.readers;

import com.checkmarx.configprovider.GeneralGitDownloader;
import com.checkmarx.configprovider.dto.GeneralRepoDto;
import com.checkmarx.configprovider.dto.ResourceType;
import com.checkmarx.configprovider.dto.interfaces.ConfigReader;
import com.typesafe.config.Config;
import org.apache.commons.lang3.StringUtils;

import javax.naming.ConfigurationException;
import java.io.File;
import java.util.Optional;

public class GeneralGitReader extends Parsable implements ConfigReader {
    private String cxConfigDefault = "cx.config";
    private final GeneralGitDownloader downloader = new GeneralGitDownloader();
    private FileReader fileReader;

    public GeneralGitReader(GeneralRepoDto repoDto, String configFileName) throws ConfigurationException {
        Optional.ofNullable(configFileName).filter(StringUtils::isNotBlank).ifPresent(cxConfig -> cxConfigDefault = cxConfig);
        readConfigFile(repoDto);
    }

    public GeneralGitReader(GeneralRepoDto repoDto) throws ConfigurationException {
        readConfigFile(repoDto);
    }

    private void readConfigFile(GeneralRepoDto repoDto) throws ConfigurationException {
        String workDirPath = downloader.downloadRepoFilesAndGetPath(repoDto);
        fileReader = new FileReader(ResourceType.YAML, workDirPath + File.separator + cxConfigDefault);
    }

    public void close() {
        downloader.closeAndDelete();
    }

    @Override
    public String getName() {
        return fileReader.getName();
    }

    @Override
    Config toConfig() throws ConfigurationException {
        return fileReader.toConfig();
    }
}
