package com.checkmarx.configprovider.readers;

import com.checkmarx.configprovider.GeneralGitDownloader;
import com.checkmarx.configprovider.dto.GeneralRepoDto;
import com.checkmarx.configprovider.dto.ResourceType;
import com.checkmarx.configprovider.dto.interfaces.ConfigReader;
import com.typesafe.config.Config;
import org.apache.commons.lang3.StringUtils;

import javax.naming.ConfigurationException;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class GeneralGitReader extends Parsable implements ConfigReader {
    private String cxConfigDefault = "cx.config";
    private String defaultFolder = ".checkmarx";
    private final GeneralGitDownloader downloader = new GeneralGitDownloader();
    private GeneralRepoDto repoDto;
    private FileReader fileReader;
    private ListReaders downloadedResource;

    public GeneralGitReader(GeneralRepoDto repoDto,String folder, String configFileName) throws ConfigurationException {
        Optional.ofNullable(configFileName).filter(StringUtils::isNotBlank).ifPresent(cxConfig -> cxConfigDefault = cxConfig);
        Optional.ofNullable(folder).filter(StringUtils::isNotBlank).ifPresent(cxFolder -> cxConfigDefault = cxFolder);
        this.repoDto = repoDto;
    }

    public GeneralGitReader(GeneralRepoDto repoDto) throws ConfigurationException {
        this.repoDto = repoDto;
    }

    public void close() {
        downloader.closeAndDelete();
    }

    @Override
    public String getName() {
        return downloadedResource.getName();
    }

    @Override
    Config toConfig() throws ConfigurationException {
        //load config Ymls from .checkmarx folder (default) and other from folders set
        List<Parsable> listRawConfigYmls = downloader.downloadRepoFiles(repoDto, Arrays.asList(defaultFolder), cxConfigDefault);
        ListReaders listReaders = new ListReaders();

        for (Parsable reader: listRawConfigYmls) {
            listReaders.add((ConfigReader) reader);
        }

        this.downloadedResource = listReaders;

        //parse will apply configuration file based on their order in the list
        //meaning ymls override configuration elements with the same name and path in config-as-code
        return listReaders.toConfig();
    }
}
