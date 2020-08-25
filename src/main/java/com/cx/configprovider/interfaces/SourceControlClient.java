package com.cx.configprovider.interfaces;

import com.cx.configprovider.dto.RepoDto;

import java.util.List;

public interface SourceControlClient {
    

    String downloadFileContent(String path, String filename, RepoDto repo);

    List<String> getDirectoryFilenames(RepoDto repoResource, String path);
}
