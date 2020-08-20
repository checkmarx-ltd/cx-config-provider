package com.cx.configprovider;

import com.cx.configprovider.dto.*;
import com.cx.configprovider.dto.interfaces.ConfigResource;
import com.cx.configprovider.resource.RawResourceImpl;
import com.cx.configprovider.exceptions.ConfigProviderException;
import com.cx.configprovider.interfaces.ConfigLoader;
import com.cx.configprovider.interfaces.SourceControlClient;
import lombok.extern.slf4j.Slf4j;

import javax.naming.ConfigurationException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Slf4j
public class RemoteRepoDownloader implements ConfigLoader {
    private static final int SUPPORTED_FILE_COUNT = 1;

    private static final EnumMap<SourceProviderType, Class<? extends SourceControlClient>> sourceProviderMapping;

    static {
        sourceProviderMapping = new EnumMap<>(SourceProviderType.class);
        sourceProviderMapping.put(SourceProviderType.GITHUB, GitHubClient.class);
    }

    private RemoteRepo remoteRepo;


    @Override
    public List<ConfigResource> getConfigAsCode(RemoteRepo repo, List<String> folders, String nameToFind) throws ConfigurationException {
        log.info("Searching for a config-as-code file in a remote repo");
        validate(repo);

        this.remoteRepo = repo;

        SourceControlClient client = determineSourceControlClient();
        
            for (String folder : folders) {
                List<String> filenames = client.getDirectoryFilenames(repo, folder);

                String nameFound = filenames.stream()
                        .filter(name -> nameToFind.equals(name))
                        .findAny()
                        .orElse(null);
                
                if(nameFound!=null){
                    return getFileContent(client, repo, folder, Arrays.asList(nameFound));
                }
                else if (filenames.size() > 0) {
                    return getFileContent(client, repo, folder, filenames);
                }
            }
            
        log.info("No config files were found for repo: " + 
                repo.getRepoName() +
                " in folders: " + folders );
        
        return new LinkedList<>();
        
    }

    private SourceControlClient determineSourceControlClient() {
        SourceProviderType providerType = remoteRepo.getSourceProviderType();
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

    private List<ConfigResource> getFileContent(SourceControlClient client, RemoteRepo repo, String folder, List<String> filenames) throws ConfigurationException {
        List<ConfigResource> resources = new LinkedList<>();
        if (filenames.isEmpty()) {
            throw new IllegalArgumentException("file names can not be empty");
        } else  {
            for(String filename : filenames) {
                String fileContent = client.downloadFileContent(folder, filenames.get(0), repo);
                log.info("Config-as-code was found with content length: {}", fileContent.length());
                RawResourceImpl configResourceImpl = new RawResourceImpl(ResourceType.JSON, fileContent, filenames.get(0));
                resources.add(configResourceImpl);
            }
            return resources;
         }
    }

//    private void throwInvalidCountException(List<String> filenames, String folder) {
//        String message = String.format(
//                "Found %d files in the '%s' directory. Only %d config-as-code file is currently supported.",
//                filenames.size(),
//                folder,
//                SUPPORTED_FILE_COUNT);
//        throw new ConfigProviderException(message);
//    }

    private static void validate(RemoteRepo configLocation) {
         Objects.requireNonNull(configLocation, "Repository must not be null.");
    }
}
