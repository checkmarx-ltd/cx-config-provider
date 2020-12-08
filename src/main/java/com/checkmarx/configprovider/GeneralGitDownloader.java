package com.checkmarx.configprovider;

import com.checkmarx.configprovider.dto.GeneralRepoDto;
import com.google.common.io.Files;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.util.FS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import static com.checkmarx.configprovider.dto.ProtocolType.HTTP;

public class GeneralGitDownloader {
    private Git git;
    private static Logger log = LoggerFactory.getLogger(GeneralGitDownloader.class);


    public String downloadRepoFilesAndGetPath(GeneralRepoDto repoDto) {
        File temp = Files.createTempDir();

        CloneCommand cloneCommand = Git.cloneRepository()
                .setBranch(repoDto.getSrcRef())
                .setProgressMonitor(new TextProgressMonitor())
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
        try {
            java.nio.file.Files.deleteIfExists(directory.toPath());
        } catch (IOException e) {
            log.error(String.format("couldn't delete file %s", directory.getAbsolutePath()));
        }

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
