package com.cx.configprovider;

import com.cx.configprovider.dto.*;
import com.cx.configprovider.dto.interfaces.ConfigResource;
import com.cx.configprovider.resource.RawResourceImpl;
import com.cx.configprovider.exceptions.ConfigProviderException;
import com.cx.configprovider.interfaces.ConfigLoader;
import com.cx.configprovider.interfaces.SourceControlClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.naming.ConfigurationException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
public class RemoteRepoDownloader implements ConfigLoader {
    private static final int SUPPORTED_FILE_COUNT = 1;

    private static final EnumMap<SourceProviderType, Class<? extends SourceControlClient>> sourceProviderMapping;

    static {
        sourceProviderMapping = new EnumMap<>(SourceProviderType.class);
        sourceProviderMapping.put(SourceProviderType.GITHUB, GitHubClient.class);
    }

    private RemoteRepo remoteRepo;


    public ConfigResource loadFileByName(SourceControlClient client, RemoteRepo repo, String folder, String fileToFind, List<String> filenames ) throws ConfigurationException {
        
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


    public List<ConfigResource> loadFileBySuffix(SourceControlClient client, RemoteRepo repo, String folder, String suffix, List<String> folderFiles ) throws ConfigurationException {

        List<ConfigResource> emptyList = new LinkedList<>();
        
        if(StringUtils.isEmpty(suffix)){
            return emptyList;
        }
        List matchingFiles = folderFiles.stream()
                .filter(name -> name.endsWith(suffix))
                .collect(Collectors.toList());
        
        if(matchingFiles.size()>0){
            return downloadFiles(client, repo, folder,matchingFiles);
        }else {
            return emptyList;
        }
    }
    
    @Override
    public List<ConfigResource> downloadRepoFiles(RemoteRepo repo, List<String> folders, String nameToFind, String suffixToFind) throws ConfigurationException {
        log.info("Searching for a config-as-code file in a remote repo");
        validate(repo);

        this.remoteRepo = repo;
        List<ConfigResource> resources = new LinkedList<>();
        
        SourceControlClient client = determineSourceControlClient();

        for (String folder : folders) {
            List<String> filenames = client.getDirectoryFilenames(repo, folder);

            ConfigResource specificFile = loadFileByName(client, repo, folder, nameToFind, filenames);

            if (specificFile != null) {
                resources = Arrays.asList(specificFile);
            } else {
                resources = loadFileBySuffix(client, repo, folder, suffixToFind, filenames);
            }
            if (resources.size() == 0) {
                resources = downloadFiles(client, repo, folder, filenames);
            }
        }

        List<String> resourceNames = resources.stream().map(resource -> resource.getName().concat(" ")).collect(Collectors.toList());

        log.info("Config files " + resourceNames + "\nwere found for repo: " + 
                repo.getRepoName() +
                " in folders: " + folders );
        
        return resources;
        
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

    private List<ConfigResource> downloadFiles(SourceControlClient client, RemoteRepo repo, String folder, List<String> filenames) throws ConfigurationException {
        List<ConfigResource> resources = new LinkedList<>();
        if (filenames == null || filenames.isEmpty()) {
            throw new IllegalArgumentException("file names can not be empty");
        } else  {
            filenames.stream().sorted().forEachOrdered(filename ->{
                String fileContent = client.downloadFileContent(folder, filename, repo);
                log.info("Config-as-code was found with content length: {}", fileContent.length());
                RawResourceImpl configResourceImpl = null;
                try {
                    configResourceImpl = new RawResourceImpl(fileContent, filename);
                } catch (ConfigurationException e) {
                   throw new RuntimeException(e);
                }
                resources.add(configResourceImpl);
            });
            
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
