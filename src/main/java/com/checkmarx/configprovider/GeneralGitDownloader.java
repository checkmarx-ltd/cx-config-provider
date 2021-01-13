package com.checkmarx.configprovider;

import com.checkmarx.configprovider.dto.GeneralRepoDto;
import com.checkmarx.configprovider.dto.RepoDto;
import com.checkmarx.configprovider.dto.ResourceType;
import com.checkmarx.configprovider.dto.interfaces.ConfigReader;
import com.checkmarx.configprovider.interfaces.SourceControlClient;
import com.checkmarx.configprovider.readers.FileContentReader;
import com.checkmarx.configprovider.readers.FileReader;
import com.checkmarx.configprovider.readers.Parsable;
import com.google.common.io.Files;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.ConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.checkmarx.configprovider.dto.ProtocolType.HTTP;

public class GeneralGitDownloader {
    private Git git;
    private static Logger log = LoggerFactory.getLogger(GeneralGitDownloader.class);


    private String downloadRepoFilesAndGetPath(GeneralRepoDto repoDto) {
        log.debug(String.format("downloading configuration files from %s ,please wait ..", repoDto.getSrcUrl()));
        File temp = Files.createTempDir();

        CloneCommand cloneCommand = Git.cloneRepository()
                .setBranch(repoDto.getSrcRef())
                .setURI(repoDto.getSrcUrl())
                .setDirectory(temp);

        if (HTTP == repoDto.getProtocolType()) {
            if (StringUtils.isNotEmpty(repoDto.getSrcUserName()) && StringUtils.isNotEmpty(repoDto.getSrcPass())) {
                cloneCommand.setCredentialsProvider(
                        new UsernamePasswordCredentialsProvider(repoDto.getSrcUserName(), repoDto.getSrcPass())
                );
            }
        } else {
            cloneCommand.setTransportConfigCallback(new SSHTransportSession(repoDto.getSrcPrivateKey()));
        }


        try {
            git = cloneCommand.call();
            if (log.isDebugEnabled())
                log.debug(String.format("clone complete into : %s", temp.getAbsolutePath()));
        } catch (GitAPIException e) {
            closeAndDelete();
            log.warn("couldn't load config file from remote for the following reason");
            return "";
        }

        return temp.getAbsolutePath();

    }


    public Parsable loadFileByName(String workDirPath, String folder, String fileToFind, List<String> filenames) {
        return Optional.ofNullable(fileToFind)
                .filter(filenames::contains)
                .map(foundFile -> downloadFiles(workDirPath, folder, Collections.singletonList(foundFile)).get(0))
                .orElse(null);
    }


    public List<Parsable> downloadRepoFiles(GeneralRepoDto repo, List<String> folders, String nameToFind) {
        log.info("Searching for a config-as-code file in a remote repo");

        List<Parsable> resources = new LinkedList<>();
        String workDirPath = downloadRepoFilesAndGetPath(repo);


        if (workDirPath.isEmpty())
            return Collections.EMPTY_LIST;

        //if folder set is empty then look for root folder
        if (folders.isEmpty() || (folders.size() == 1 && "/".equals(folders.get(0)))) {
            folders.clear();
            folders.add("");
        }

        for (String folder : folders) {
            List<String> filenames = getDirectoryFilenames(workDirPath, folder);

            Parsable specificFile = loadFileByName(workDirPath, folder, nameToFind, filenames);

            resources = Optional.ofNullable(specificFile)
                    .map(Arrays::asList)
                    .orElse(Collections.EMPTY_LIST);

            if (resources.isEmpty() && filenames != null && !filenames.isEmpty()) {
                resources = downloadFiles(workDirPath, folder, filenames);
            }
        }

        List<String> resourceNames = resources.stream().map(resource -> ((ConfigReader) resource).getName().concat(" ")).collect(Collectors.toList());

        if (!resourceNames.isEmpty()) {
            //repo url without user information if exists
            String repoUrl = !repo.getSrcUrl().contains("@") ? repo.getSrcUrl() :
                    repo.getSrcUrl().substring(repo.getSrcUrl().indexOf("@"));

            log.info("Config files " + resourceNames + "\nwere found for repo: " +
                    repoUrl +
                    " in folders: " + folders);
        }
        return resources;

    }

    private List<String> getDirectoryFilenames(String workDirPath, String folder) {
        String path = StringUtils.isNotBlank(folder) ? File.separator + folder : "";
        File workDir = new File(workDirPath + path);

        if (workDir.exists())
            return Stream.of(workDir.listFiles()).map(File::getName).collect(Collectors.toList());
        else
            return Collections.EMPTY_LIST;
    }

    private List<Parsable> downloadFiles(String workDirPath, String folder, List<String> filenames) {
        List<Parsable> resources = new LinkedList<>();
        if (filenames == null || filenames.isEmpty()) {
            throw new IllegalArgumentException("file names can not be empty");
        }
        filenames.stream().sorted().forEachOrdered(filename -> {
            try {
                String path = StringUtils.isNotBlank(folder) ? File.separator + folder : "";
                File file = new File(workDirPath + path + File.separator + filename);
                FileInputStream fileInputStream = new FileInputStream(file.getPath());
                String yamlContent = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8);
                FileContentReader reader = new FileContentReader(yamlContent, filename, ResourceType.YAML);
                resources.add(reader);

                fileInputStream.close();
                file.delete();

            } catch (IOException e) {
            }
        });

        return resources;
    }

    public void closeAndDelete() {
        if (git == null)
            return;

        final File directory = git.getRepository().getWorkTree();
        git.close();
        delete(directory);
    }

    private void delete(File directory) {
        final File[] files;
        if (directory.isDirectory() && (files = directory.listFiles()) != null)
            Stream.of(files).forEach(this::delete);

        if (!directory.delete())
            log.warn(String.format("couldn't delete file %s", directory.getAbsolutePath()));
    }

    private class SSHTransportSession implements TransportConfigCallback {
        private String privateKeyPath;

        public SSHTransportSession(String privateKeyPath) {
            this.privateKeyPath = privateKeyPath;
        }

        @Override
        public void configure(Transport transport) {
            if (transport instanceof SshTransport) {
                SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
                    @Override
                    protected void configure(OpenSshConfig.Host host, Session session) {
                        session.setConfig("StrictHostKeyChecking", "no");
                    }

                    @Override
                    protected JSch createDefaultJSch(FS fs) throws JSchException {
                        JSch jSch = super.createDefaultJSch(fs);
                        if (StringUtils.isNotEmpty(privateKeyPath))
                            jSch.addIdentity(privateKeyPath, "super-secret-passphrase".getBytes());
                        return jSch;
                    }
                };

                ((SshTransport) transport).setSshSessionFactory(sshSessionFactory);
            }

        }
    }
}
