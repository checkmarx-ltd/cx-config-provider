package com.cx.configprovider.interfaces;

import com.cx.configprovider.dto.ConfigLocation;

import java.util.List;

public interface SourceControlClient {
    String downloadFileContent(ConfigLocation configLocation, String filename);

    List<String> getDirectoryFilenames(ConfigLocation configLocation);
}
