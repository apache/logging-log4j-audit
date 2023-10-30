/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.logging.log4j.catalog.git.dao;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.catalog.api.CatalogData;
import org.apache.logging.log4j.catalog.api.dao.AbstractCatalogReader;
import org.apache.logging.log4j.catalog.api.dao.CatalogDao;
import org.apache.logging.log4j.catalog.api.exception.CatalogModificationException;
import org.apache.logging.log4j.catalog.api.exception.CatalogNotFoundException;
import org.apache.logging.log4j.catalog.api.exception.CatalogReadException;
import org.apache.logging.log4j.catalog.api.util.CatalogEventFilter;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;

public class GitCatalogDao extends AbstractCatalogReader implements CatalogDao {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String DEFAULT_CATALOG_PATH = "src/main/resources/catalog.json";

    private final ObjectMapper mapper;

    private CredentialsProvider credentialsProvider = null;
    private TransportConfigCallback transportConfigCallback = null;
    private String remoteRepoUri = null;
    private String localRepoPath = null;
    private String catalogPath = DEFAULT_CATALOG_PATH;
    private String branch = null;

    private Repository localRepo = null;
    private Git git = null;
    private File catalogFile = null;

    public GitCatalogDao() {
        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_COMMENTS);
        mapper = new ObjectMapper(factory).enable(SerializationFeature.INDENT_OUTPUT);
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter("catalogEvent", new CatalogEventFilter());
        mapper.setFilterProvider(filterProvider);
    }

    public CredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

    public void setCredentialsProvider(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    public TransportConfigCallback getTransportConfigCallback() {
        return transportConfigCallback;
    }

    public void setTransportConfigCallback(TransportConfigCallback transportConfigCallback) {
        this.transportConfigCallback = transportConfigCallback;
    }

    public String getRemoteRepoUri() {
        return remoteRepoUri;
    }

    public void setRemoteRepoUri(String remoteRepoUri) {
        this.remoteRepoUri = remoteRepoUri;
    }

    public String getLocalRepoPath() {
        return localRepoPath;
    }

    public void setLocalRepoPath(String localRepoPath) {
        this.localRepoPath = localRepoPath;
    }

    public String getCatalogPath() {
        return catalogPath;
    }

    public void setCatalogPath(String catalogPath) {
        this.catalogPath = catalogPath;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    @Override
    public LocalDateTime getLastUpdated() {
        if (localRepo == null) {
            updateRepo();
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(catalogFile.lastModified()),
                ZoneId.systemDefault());
    }

    @Override
    public synchronized CatalogData read() {
        updateRepo();
        if (catalogFile == null) {
            throw new CatalogNotFoundException();
        }

        if (!catalogFile.exists() || !catalogFile.canRead()) {
            throw new CatalogReadException("Catalog " + catalogFile.getAbsolutePath() + " is not readable.");
        }

        try {
            catalogData = mapper.readValue(catalogFile, CatalogData.class);
            return catalogData;
        } catch (IOException ioe) {
            throw new CatalogReadException("Error reading catalog from " + catalogFile.getAbsolutePath(), ioe);
        }
    }

    @Override
    public void write(CatalogData data) {
        File localRepoFile = new File(localRepoPath);
        if (!localRepoFile.exists() || !localRepoFile.canWrite()) {
            throw new CatalogModificationException("Catalog is not writable: " + localRepoFile.getAbsolutePath());
        }

        try (FileWriter writer = new FileWriter(catalogFile)){
            String text = mapper.writeValueAsString(data);
            writer.write(text);
        } catch (IOException ioException) {
            throw new CatalogModificationException("Unable to write catalog file.", ioException);
        }

        try (Git git = Git.open(localRepoFile)) {
            git.add().addFilepattern(catalogPath).call();
            git.commit().setMessage("Catalog updated").call();
            updateRepo();
            PushCommand pushCommand = git.push();
            if (credentialsProvider != null) {
                pushCommand.setCredentialsProvider(credentialsProvider);
            }
            if (transportConfigCallback != null) {
                pushCommand.setTransportConfigCallback(transportConfigCallback);
            }
            pushCommand.call();
        } catch (GitAPIException | IOException ex) {
            throw new CatalogModificationException("Unable to modify catalog", ex);
        }
    }

    @Override
    public String readCatalog() {
        return null;
    }

    private void updateRepo() {

        File localRepoFile = new File(localRepoPath);
        if (!localRepoFile.exists()) {
            LOGGER.debug("local git repo {} does not exist - creating it", localRepoPath);
            localRepoFile.getParentFile().mkdirs();
            CloneCommand cloneCommand = Git.cloneRepository().setURI(remoteRepoUri).setDirectory(localRepoFile);
            if (branch != null) {
                cloneCommand.setBranch(branch);
            }
            if (credentialsProvider != null) {
                cloneCommand.setCredentialsProvider(credentialsProvider);
            }
            if (transportConfigCallback != null) {
                cloneCommand.setTransportConfigCallback(transportConfigCallback);
            }
            try (Git git = cloneCommand.call()) {
                catalogFile = new File(localRepoFile, catalogPath);
            } catch (Exception ex) {
                throw new CatalogNotFoundException("Unable to clone remote catalog at " + remoteRepoUri + " to " + localRepoPath, ex);
            }
        } else {
            try {
                LOGGER.debug("local git repo {} exists - updating", localRepoPath);
                localRepo = new FileRepository(localRepoPath  + "/.git");
                catalogFile = new File(localRepoFile, catalogPath);
                git = new Git(localRepo);
                PullCommand pullCommand = git.pull();
                try {
                    if (credentialsProvider != null) {
                        pullCommand.setCredentialsProvider(credentialsProvider);
                    }
                    if (transportConfigCallback != null) {
                        pullCommand.setTransportConfigCallback(transportConfigCallback);
                    }
                    pullCommand.call();
                } catch (GitAPIException gitApiException) {
                    LOGGER.error("Exception", gitApiException);
                }
            } catch (Exception exception) {
                throw new CatalogReadException("Unable to pull remote catalog at " + remoteRepoUri + " to " + localRepoPath, exception);
            }
        }
    }
}
