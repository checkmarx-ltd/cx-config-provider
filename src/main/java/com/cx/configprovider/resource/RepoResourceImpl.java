package com.cx.configprovider.resource;

import com.cx.configprovider.dto.interfaces.ConfigResource;
import com.cx.configprovider.dto.RemoteRepo;
import com.cx.configprovider.RemoteRepoDownloader;
import com.cx.configprovider.exceptions.ConfigProviderException;
import com.typesafe.config.Config;
import lombok.Builder;
import lombok.Getter;

import javax.naming.ConfigurationException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Getter

public class RepoResourceImpl implements ConfigResource {

    private static final String DEFAULT_SEARCH_DIRECTORY = ".checkmarx";
    private static final String REPO_ROOT = "";
    private static final String CX_CONFIG = "cx.config";
    private String configAsCode;
    
    RemoteRepo remoteRepo;
    RemoteRepoDownloader downloader = new RemoteRepoDownloader();
  
    List<String> foldersToSearch = new LinkedList<String>();
    
    public RepoResourceImpl(RemoteRepo repo, String configAsCode, List<String> foldersToSearch){
        this.remoteRepo = repo;
        this.configAsCode = configAsCode;
        this.foldersToSearch = foldersToSearch;
    }

    public void setConfigAsCode(String configAsCode) {
        this.configAsCode = configAsCode;
    }

    public void setFoldersToSearch(List<String> foldersToSearch) {
        this.foldersToSearch = foldersToSearch;
    }

    /**
     * loads repository with default configuration files to refer to
     * @param repo
     */
    public RepoResourceImpl(RemoteRepo repo ){
        this.remoteRepo = repo;
        this.foldersToSearch.add(DEFAULT_SEARCH_DIRECTORY);
        setConfigAsCode(CX_CONFIG);
    }
    

    @Override
    public Config parse() throws ConfigurationException {
        
        List<ConfigResource> listRawConfigAsCode = downloader.getConfigAsCode(getRemoteRepo(), Arrays.asList(REPO_ROOT), configAsCode);

        //Search for config as code at the root of the repo
        if (listRawConfigAsCode.size() == 1) {
            return listRawConfigAsCode.get(0).parse();
        }else {
            //search for YML files
            List<ConfigResource> listRaw = downloader.getConfigAsCode(getRemoteRepo(), getFoldersToSearch(), null);
            return new MultipleResourcesImpl(listRaw).parse();
        }

    }
    

}
