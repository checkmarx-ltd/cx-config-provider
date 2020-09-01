package com.checkmarx.configprovider.interfaces;

import com.checkmarx.configprovider.dto.RepoDto;

import java.util.List;

public interface SourceControlClient {
    

    String downloadFileContent(String path, String filename, RepoDto repo);

    List<String> getDirectoryFilenames(RepoDto repoResource, String path);
}
