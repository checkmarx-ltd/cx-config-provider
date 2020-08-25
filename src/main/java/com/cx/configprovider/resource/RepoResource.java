package com.cx.configprovider.resource;

import com.cx.configprovider.dto.RepoDto;
import com.cx.configprovider.RemoteRepoDownloader;
import com.cx.configprovider.dto.SourceProviderType;
import com.cx.configprovider.dto.interfaces.ConfigResource;
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
public class RepoResource extends ParsableResource implements ConfigResource {

    private static final String DEFAULT_SEARCH_DIRECTORY = ".checkmarx";
    private static final String REPO_ROOT = "";
    private static final String CX_CONFIG = "cx.config";
    private static final String YML = "yml";

    
    private static final RemoteRepoDownloader downloader = new RemoteRepoDownloader();
    
    private String configAsCodeFileName;
    private List<String> foldersToSearch = new LinkedList<String>();
    private MultipleResources downloadedResource;

    private RepoDto repoDto;

    /**
     * @param apiBaseUrl  repository URL
     * @param namespace namespace
     * @param repoName repository name 
     * @param branch repository branch
     * @param accessToken repository access Token
     * @param sourceProviderType sourceProviderType
     * @param configAsCodeFileName a name of config-as-code file under root
     * @param foldersToSearch a list of paths under repo root where the YML configuration files will be located
     */
    public RepoResource(String apiBaseUrl, String namespace, String repoName, String branch, String accessToken, SourceProviderType sourceProviderType, String configAsCodeFileName, List<String> foldersToSearch) {

        buildRemoteRepo(apiBaseUrl, namespace, repoName, branch, accessToken, sourceProviderType);
        this.configAsCodeFileName = configAsCodeFileName;
        this.foldersToSearch = foldersToSearch;
    }


    /**
     * Constructor which has only the repository details. Configuration files wll be located under the default 
     * locations: config-as-code default name will be {@value #CX_CONFIG} and default configuration YML locations 
     * will be {@value #DEFAULT_SEARCH_DIRECTORY}
     * @param apiBaseUrl  repository URL
     * @param namespace namespace
     * @param repoName repository name 
     * @param branch repository branch
     * @param accessToken repository access Token
     * @param sourceProviderType repository type 
     */
    public RepoResource( String apiBaseUrl, String namespace, String repoName, String branch, String accessToken, SourceProviderType sourceProviderType) {

        buildRemoteRepo(apiBaseUrl, namespace, repoName, branch, accessToken, sourceProviderType);
        this.foldersToSearch.add(DEFAULT_SEARCH_DIRECTORY);
        setConfigAsCodeFileName(CX_CONFIG);
    }

    /**
     * Constructor which has only the repository details. 
     * Default configuration YML locations will be {@value #DEFAULT_SEARCH_DIRECTORY}
     * @param apiBaseUrl  repository URL
     * @param namespace namespace
     * @param repoName repository name 
     * @param branch repository branch
     * @param accessToken repository access Token
     * @param sourceProviderType repository type 
     * @param configAsCodeFileName a name of config-as-code file under root
     */
    public RepoResource( String apiBaseUrl, String namespace, String repoName, String branch, String accessToken, SourceProviderType sourceProviderType, String configAsCodeFileName) {

        buildRemoteRepo(apiBaseUrl, namespace, repoName, branch, accessToken, sourceProviderType);
        this.foldersToSearch.add(DEFAULT_SEARCH_DIRECTORY);
        this.configAsCodeFileName = configAsCodeFileName;

    }

    /**
     * Constructor which has only the repository details.  
     * config-as-code default name will be {@value #CX_CONFIG} 
     * @param apiBaseUrl  repository URL
     * @param namespace namespace
     * @param repoName repository name 
     * @param branch repository branch
     * @param accessToken repository access Token
     * @param sourceProviderType repository type 
     * @param foldersToSearch a list of paths under repo root where the YML configuration files will be located
     */
    public RepoResource( String apiBaseUrl, String namespace, String repoName, String branch, String accessToken, SourceProviderType sourceProviderType, List<String> foldersToSearch) {

        buildRemoteRepo(apiBaseUrl, namespace, repoName, branch, accessToken, sourceProviderType);
        this.foldersToSearch = foldersToSearch;
        setConfigAsCodeFileName(CX_CONFIG);
    }
    
    public void setConfigAsCodeFileName(String configAsCodeFileName) {
        this.configAsCodeFileName = configAsCodeFileName;
    }

    public void setFoldersToSearch(List<String> foldersToSearch) {
        this.foldersToSearch = foldersToSearch;
    }
    


    /**
     * Parses the configuration files in repository according to the following order:
     * 1. config-as-code in the root of the repository.
     *   (The name of the config-as-code file is set by {@link #setConfigAsCodeFileName(String)} )
     * 2. yml files in .checkmarx folder in alphabetical order  
     * yml files elements will truncate the elements with the same name and path in config-as-code 
     * @return Config object representing ap arsed configuration consisting of all resources
     * @throws ConfigurationException exception
     */
    @Override
    Config loadConfig() throws ConfigurationException {
        
        //first load config-as-code from the root of the repo (default) or other folder set 
        ParsableResource configAsCodeResource = downloader.downloadRepoFiles(getRepoDto(), Arrays.asList(REPO_ROOT), configAsCodeFileName, null).get(0);

        //then load config Ymls from .checkmarx folder (default) and other from folders set 
        List<ParsableResource> listRawConfigYmls = downloader.downloadRepoFiles(getRepoDto(), foldersToSearch, null, YML);

        //add config-as-code to be the first one in the list - so that hte ymls be applied over it
        //ymls truncate configuration elements with the same name and path in config-as-code
        listRawConfigYmls.add(0,configAsCodeResource);

        MultipleResources multipleResourcesImpl = new MultipleResources(listRawConfigYmls);

        this.downloadedResource = multipleResourcesImpl;
        //parse will apply configuration file based on their order in the list
        //meaning ymls truncate configuration elements with the same name and path in config-as-code
        return multipleResourcesImpl.loadConfig();

    }
    
    @Override
    public String getName() {
        return downloadedResource.getName();
    }

    
    private void buildRemoteRepo(String apiBaseUrl, String namespace, String repoName, String branch, String accessToken, SourceProviderType sourceProviderType) {
        repoDto = RepoDto.builder()
                .apiBaseUrl(apiBaseUrl)
                .repoName(repoName)
                .namespace(namespace)
                .ref(branch)
                .accessToken(accessToken)
                .sourceProviderType(sourceProviderType)
                .build();
    }

}
