package com.cx.configprovider.resource;

import com.cx.configprovider.dto.interfaces.ConfigResource;
import com.cx.configprovider.dto.RemoteRepo;
import com.cx.configprovider.RemoteRepoDownloader;
import com.typesafe.config.Config;
import lombok.Getter;

import javax.naming.ConfigurationException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * loads repository with default configuration files such as "cx.config" at the root of the folder
 * and .checkmarx folder where all the yml configuration files will be located
 */
@Getter
public class RepoResourceImpl implements ConfigResource {

    private static final String DEFAULT_SEARCH_DIRECTORY = ".checkmarx";
    private static final String REPO_ROOT = "";
    private static final String CX_CONFIG = "cx.config";
    private static final String YML = "yml";
    private String configAsCode;
    
    RemoteRepo remoteRepo;
    RemoteRepoDownloader downloader = new RemoteRepoDownloader();
  
    List<String> foldersToSearch = new LinkedList<String>();
    private MultipleResourcesImpl downloadedResource;

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


    public RepoResourceImpl(RemoteRepo repo ){
        this.remoteRepo = repo;
        this.foldersToSearch.add(DEFAULT_SEARCH_DIRECTORY);
        setConfigAsCode(CX_CONFIG);
    }


    /**
     * Parses the configuration files in repository according to the following order:
     * 1. config-as-code in the root of the repository.
     *   (The name of the config-as-code file is set by {@link #setConfigAsCode(String)} )
     * 2. yml files in .checkmarx folder in alphabetical order  
     * yml files elements will truncate the elements with the same name and path in config-as-code 
     * @return Config object representing ap arsed configuration consisting of all resources
     * @throws ConfigurationException exception
     */
    @Override
    public Config parse() throws ConfigurationException {
        
        //first load config-as-code from the root of the repo (default) or other folder set 
        ConfigResource configAsCodeResource = downloader.downloadRepoFiles(getRemoteRepo(), Arrays.asList(REPO_ROOT), configAsCode, null).get(0);

        //then load config Ymls from .checkmarx folder (default) and other from folders set 
        List<ConfigResource> listRawConfigYmls = downloader.downloadRepoFiles(getRemoteRepo(), foldersToSearch, null, YML);

        //add config-as-code to be the first one in the list - so that hte ymls be applied over it
        //ymls truncate configuration elements with the same name and path in config-as-code
        listRawConfigYmls.add(0,configAsCodeResource);

        MultipleResourcesImpl multipleResourcesImpl = new MultipleResourcesImpl(listRawConfigYmls);

        this.downloadedResource = multipleResourcesImpl;
        //parse will apply configuration file based on their order in the list
        //meaning ymls truncate configuration elements with the same name and path in config-as-code
        return multipleResourcesImpl.parse();

    }
    
    @Override
    public String getName() {
        return downloadedResource.getName();
    }

}
