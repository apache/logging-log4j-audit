/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.logging.log4j.catalog.git.config;

import javax.annotation.PreDestroy;
import java.io.File;

import org.apache.logging.log4j.catalog.git.dao.GitCatalogDao;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.FileSystemUtils;

@Configuration
@ComponentScan(basePackages = { "org.apache.logging.log4j.catalog" })
public class ApplicationConfiguration {

    private String gitLocalRepoPath;

    private String gitRemoteRepoUri;

    @Bean
    public GitCatalogDao catalogDao() {
        String tempDir = System.getProperty("java.io.tmpdir");
        gitLocalRepoPath = tempDir + "/audit/catalog";
        gitRemoteRepoUri = "https://github.com/apache/logging-log4j-audit-sample.git";
        File file = new File(gitLocalRepoPath);
        File parent = file.getParentFile();
        parent.mkdirs();
        FileSystemUtils.deleteRecursively(file);
        GitCatalogDao catalogDao = new GitCatalogDao();
        catalogDao.setLocalRepoPath(gitLocalRepoPath);
        catalogDao.setRemoteRepoUri(gitRemoteRepoUri);
        catalogDao.setCatalogPath("audit-test/catalog.json");
        //CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider("waymirec", "w4ym1r3c");
        //catalogDao.setCredentialsProvider(credentialsProvider);
        return catalogDao;
    }

    @PreDestroy
    public void destroy() {
        String tempDir = System.getProperty("java.io.tmpdir");
        gitLocalRepoPath = tempDir + "/audit/catalog";
        File file = new File(gitLocalRepoPath);
        FileSystemUtils.deleteRecursively(file);
    }


}
