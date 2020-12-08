package com.checkmarx.configprovider.downloader;

import com.checkmarx.configprovider.GeneralGitDownloader;
import com.checkmarx.configprovider.dto.GeneralRepoDto;
import com.checkmarx.configprovider.dto.ProtocolType;
import com.checkmarx.configprovider.readers.Parsable;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class GeneralRepoDownloaderTest {
    private final GeneralGitDownloader downloader = new GeneralGitDownloader();

    private GeneralRepoDto prepareHttpRequest(){
        GeneralRepoDto repodto = new GeneralRepoDto();
        repodto.setProtocolType(ProtocolType.HTTP);

        return repodto;
    }

    @Test
    public void TEST_DOWNLOAD_GITHUB_PUBLIC(){
        GeneralRepoDto repoDto = prepareHttpRequest();
        repoDto.setSrcRef("main");
        repoDto.setSrcUrl("https://github.com/cx-muhammed/sample.git");


        List<Parsable> path = downloader.downloadRepoFiles(repoDto, new ArrayList<>(),"cx");
        assertTrue(path != null && !path.isEmpty());
    }

    @Test
    public void TEST_DOWNLOAD_BITBUCKET_PUBLIC(){
        GeneralRepoDto repoDto = prepareHttpRequest();
        repoDto.setSrcRef("master");
        repoDto.setSrcUrl("https://mudex@bitbucket.org/mudex/sample.git");


        List<Parsable> path = downloader.downloadRepoFiles(repoDto, new ArrayList<>(),"cx");
        assertTrue(path != null && !path.isEmpty());
    }


    @After
    public void CLEAN_UP(){
        downloader.closeAndDelete();
    }


}
