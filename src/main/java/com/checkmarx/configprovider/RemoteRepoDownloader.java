package com.checkmarx.configprovider;

import com.checkmarx.configprovider.dto.RepoDto;
import com.checkmarx.configprovider.dto.SourceProviderType;
import com.checkmarx.configprovider.dto.interfaces.ConfigReader;
import com.checkmarx.configprovider.exceptions.ConfigProviderException;
import com.checkmarx.configprovider.readers.FileContentReader;
import com.checkmarx.configprovider.readers.Parsable;

import com.checkmarx.configprovider.interfaces.SourceControlClient;
import lombok.extern.slf4j.Slf4j;
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


    public Parsable loadFileByName(SourceControlClient client, RepoDto repo, String folder, String fileToFind, List<String> filenames )  {
        return Optional.ofNullable(fileToFind)
        .filter(filenames::contains)
        .map(foundFile -> downloadFiles(client, repo, folder, Collections.singletonList(foundFile)).get(0))
        .orElse(null);
    }


    public List<Parsable> loadFileBySuffix(SourceControlClient client, RepoDto repo, String folder, String suffix, List<String> folderFiles ) {

        List<String> matchingFiles = Optional.ofNullable(suffix)
        .map(thesuffix -> folderFiles.stream()
        .filter(name -> name.endsWith(thesuffix))
        .collect(Collectors.toList()))
        .orElse(new LinkedList<>());
        
        return matchingFiles.isEmpty() ? new LinkedList<>() : downloadFiles(client, repo, folder,matchingFiles);
    }
    

    public List<Parsable> downloadRepoFiles(RepoDto repo, List<String> folders, String nameToFind, String suffixToFind) throws ConfigurationException {
        log.info("Searching for a config-as-code file in a remote repo");
        validate(repo);

        this.repoDto = repo;
        List<Parsable> resources = new LinkedList<>();
        
        SourceControlClient client = determineSourceControlClient();

        for (String folder : folders) {
            List<String> filenames = client.getDirectoryFilenames(repo, folder);

            Parsable specificFile = loadFileByName(client, repo, folder, nameToFind, filenames);

            resources = Optional.ofNullable(specificFile)
            .map(Arrays::asList)
            .orElse(loadFileBySuffix(client, repo, folder, suffixToFind, filenames));
            
            if (resources.isEmpty() && filenames != null && !filenames.isEmpty()) {
                resources = downloadFiles(client, repo, folder, filenames);
            }
        }

        List<String> resourceNames = resources.stream().map(resource -> ((ConfigReader)resource).getName().concat(" ")).collect(Collectors.toList());

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

    private List<Parsable> downloadFiles(SourceControlClient client, RepoDto repo, String folder, List<String> filenames)  {
        List<Parsable> resources = new LinkedList<>();
        if (filenames == null || filenames.isEmpty()) {
            throw new IllegalArgumentException("file names can not be empty");
        }
        filenames.stream().sorted().forEachOrdered(filename ->{
            String fileContent = client.downloadFileContent(folder, filename, repo);
            log.info("Config-as-code was found with content length: {}", fileContent.length());
            FileContentReader configResourceImpl = new FileContentReader(fileContent, filename);
            
            resources.add(configResourceImpl);
        });
        
        return resources;
    }


    private static void validate(RepoDto configLocation) {
         Objects.requireNonNull(configLocation, "Repository must not be null.");
    }
}
