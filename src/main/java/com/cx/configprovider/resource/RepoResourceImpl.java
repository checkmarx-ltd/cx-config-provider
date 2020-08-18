package com.cx.configprovider.resource;

import com.cx.configprovider.dto.ConfigLocation;
import com.cx.configprovider.dto.interfaces.ConfigResource;
import com.cx.configprovider.dto.RemoteRepoLocation;
import com.cx.configprovider.RemoteRepoDownloader;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigParseOptions;

import javax.naming.ConfigurationException;
import java.util.LinkedList;
import java.util.List;

public class RepoResourceImpl implements ConfigResource {

    private Config config;
    RemoteRepoLocation repoLocation;
    RemoteRepoDownloader downloader = new RemoteRepoDownloader();
    List<String> pathListToSearch = new LinkedList<String>();
    
    public RepoResourceImpl(RemoteRepoLocation repoLocation){
        this.repoLocation = repoLocation;
        initPathList();
    }

    private void initPathList() {
        //TODO
    }

    @Override
    public Config parse() throws ConfigurationException {

        for (String path: pathListToSearch) {
            ConfigLocation location = ConfigLocation.builder()
                    .path(path)
                    .repoLocation(repoLocation)
                    .build();

            Config config = downloader.getConfigAsCode(location).parse();
            
            if(config != null){
                this.config = config;
                break;
            }
        }
        
        return config;
    }

}
