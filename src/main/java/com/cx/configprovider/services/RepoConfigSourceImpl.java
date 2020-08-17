package com.cx.configprovider.services;

import com.cx.configprovider.dto.ConfigLocation;
import com.cx.configprovider.dto.ConfigResource;
import com.cx.configprovider.dto.ConfigResourceImpl;
import com.cx.configprovider.dto.RemoteRepoLocation;
import com.typesafe.config.Config;
import org.apache.commons.lang3.StringUtils;

import javax.naming.ConfigurationException;
import java.util.LinkedList;
import java.util.List;

public class RepoConfigSourceImpl implements ConfigResource {

    private Config config;
    RemoteRepoLocation repoLocation;
    RemoteRepoConfigDownloader downloader = new RemoteRepoConfigDownloader();
    List<String> pathListToSearch = new LinkedList<String>();
    
    public RepoConfigSourceImpl(RemoteRepoLocation repoLocation){
        this.repoLocation = repoLocation;
        initPathList();
    }

    private void initPathList() {
        //TODO
    }

    public void parse() throws ConfigurationException {

        for (String path: pathListToSearch) {
            ConfigLocation location = ConfigLocation.builder()
                    .path(path)
                    .repoLocation(repoLocation)
                    .build();

            ConfigResource configResource = downloader.getConfigAsCode(location);
            
            if(configResource.getConfig() != null){
                config = configResource.getConfig();
                break;
            }
        }
    }
    
    @Override
    public Config getConfig() {
        return config;
    }

}
