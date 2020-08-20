package com.cx.configprovider.interfaces;

import com.cx.configprovider.dto.RemoteRepo;
import com.cx.configprovider.dto.interfaces.ConfigResource;

import javax.naming.ConfigurationException;
import java.util.List;

public interface ConfigLoader {
    
    List<ConfigResource> getConfigAsCode(RemoteRepo repo, List<String> folders, String nameToFind) throws ConfigurationException;
}
