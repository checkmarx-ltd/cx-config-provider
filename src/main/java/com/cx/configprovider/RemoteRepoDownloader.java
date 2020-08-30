package com.cx.configprovider;

import com.cx.configprovider.dto.*;
import com.cx.configprovider.dto.interfaces.ConfigResource;

import com.cx.configprovider.resource.ParsableResource;
import com.cx.configprovider.resource.FileContentResource;
import com.cx.configprovider.exceptions.ConfigProviderException;
import com.cx.configprovider.interfaces.SourceControlClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.naming.ConfigurationException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import java.util.stream.Collectors;

@Slf4j
public class RemoteRepoDownloader {

    private static final EnumMap<SourceProviderType, Class<? extends SourceControlClient>> sourceProviderMapping;

    static {
        sourceProviderMapping = new EnumMap<>(SourceProviderType.class);
        sourceProviderMapping.put(SourceProviderType.GITHUB, GitHubClient.class);
    }

    private RepoDto repoDto;


    public ParsableResource loadFileByName(SourceControlClient client, RepoDto repo, String folder, String fileToFind, List<String> filenames )  {
        
        if(StringUtils.isEmpty(fileToFind)){
            return null;
        }
        String nameFound = filenames.stream()
                .filter(name -> fileToFind.equals(name))
                .findAny()
                .orElse(null);

        if(nameFound!=null){
            return downloadFiles(client, repo, folder, Arrays.asList(nameFound)).get(0);
        }
        return null;
    }


    public List<ParsableResource> loadFileBySuffix(SourceControlClient client, RepoDto repo, String folder, String suffix, List<String> folderFiles ) {

        List<ParsableResource> emptyList = new LinkedList<>();
        
        if(StringUtils.isEmpty(suffix)){
            return emptyList;
        }
        List<String> matchingFiles = folderFiles.stream()
                .filter(name -> name.endsWith(suffix))
                .collect(Collectors.toList());
        
        if(!matchingFiles.isEmpty()){
            return downloadFiles(client, repo, folder,matchingFiles);
        }else {
            return emptyList;
        }
    }
    

    public List<ParsableResource> downloadRepoFiles(RepoDto repo, List<String> folders, String nameToFind, String suffixToFind) throws ConfigurationException {
        log.info("Searching for a config-as-code file in a remote repo");
        validate(repo);

        this.repoDto = repo;
        List<ParsableResource> resources = new LinkedList<>();
        
        SourceControlClient client = determineSourceControlClient();

        for (String folder : folders) {
            List<String> filenames = client.getDirectoryFilenames(repo, folder);

            ParsableResource specificFile = loadFileByName(client, repo, folder, nameToFind, filenames);

            if (specificFile != null) {
                resources = Arrays.asList(specificFile);
            } else {
                resources = loadFileBySuffix(client, repo, folder, suffixToFind, filenames);
            }
            if (resources.isEmpty()) {
                resources = downloadFiles(client, repo, folder, filenames);
            }
        }

        List<String> resourceNames = resources.stream().map(resource -> ((ConfigResource)resource).getName().concat(" ")).collect(Collectors.toList());

        log.info("Config files " + resourceNames + "\nwere found for repo: " + 
                repo.getRepoName() +
                " in folders: " + folders );
        
        return resources;
        
    }

    private SourceControlClient determineSourceControlClient() {
        SourceProviderType providerType = repoDto.getSourceProviderType();
        log.debug("Determining the client for the {} source control provider", providerType);

        Class<? extends SourceControlClient> clientClass = getClientClass(providerType);
        SourceControlClient result = getClientInstance(clientClass);

        log.debug("Using {} to access the repo", result.getClass().getName());
        return result;
    }

    private static SourceControlClient getClientInstance(Class<? extends SourceControlClient> clientClass) {
        SourceControlClient result;
        try {
            result = clientClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            String message = String.format("Unable to create an instance of %s.",
                    SourceProviderType.class.getSimpleName());
            throw new ConfigProviderException(message, e);
        }
        return result;
    }

    private static Class<? extends SourceControlClient> getClientClass(SourceProviderType sourceProviderType) {
        Class<? extends SourceControlClient> clientClass = sourceProviderMapping.get(sourceProviderType);
        if (clientClass == null) {
            String message = String.format("The '%s' %s is not supported",
                    sourceProviderType,
                    SourceProviderType.class.getSimpleName());
            throw new ConfigProviderException(message);
        }
        return clientClass;
    }

    private List<ParsableResource> downloadFiles(SourceControlClient client, RepoDto repo, String folder, List<String> filenames)  {
        List<ParsableResource> resources = new LinkedList<>();
        if (filenames == null || filenames.isEmpty()) {
            throw new IllegalArgumentException("file names can not be empty");
        } else  {
            filenames.stream().sorted().forEachOrdered(filename ->{
                String fileContent = client.downloadFileContent(folder, filename, repo);
                log.info("Config-as-code was found with content length: {}", fileContent.length());
                FileContentResource configResourceImpl = null;

                configResourceImpl = new FileContentResource(fileContent, filename);
               
                resources.add(configResourceImpl);
            });
            
            return resources;
         }
    }


    private static void validate(RepoDto configLocation) {
         Objects.requireNonNull(configLocation, "Repository must not be null.");
    }
}
