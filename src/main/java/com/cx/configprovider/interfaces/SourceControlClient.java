package com.cx.configprovider.interfaces;

import com.cx.configprovider.dto.RemoteRepo;
import com.cx.configprovider.resource.RepoResourceImpl;

import java.util.List;

public interface SourceControlClient {
    

    String downloadFileContent(String path, String filename, RemoteRepo repo);

    List<String> getDirectoryFilenames(RemoteRepo repoResource, String path);
}
