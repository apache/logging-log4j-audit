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
package org.apache.logging.log4j.catalog.config;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.catalog.api.dao.CatalogDao;
import org.apache.logging.log4j.catalog.api.util.CatalogEventFilter;
import org.apache.logging.log4j.catalog.git.dao.GitCatalogDao;
import org.apache.logging.log4j.catalog.security.LocalAuthorizationInterceptor;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FS;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Configuration
@ComponentScan(basePackages = {"org.apache.logging.log4j.catalog"})
//@PropertySource(value = "classpath:catalog-${env:}config.properties", ignoreResourceNotFound = true)
public class WebMvcAppContext extends WebMvcConfigurerAdapter implements ApplicationContextAware {

    private static final Logger LOGGER = LogManager.getLogger(WebMvcAppContext.class);

    @Autowired
    ConfigurationService configurationService;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("products").setViewName("products");
        registry.addViewController("categories").setViewName("categories");
        registry.addViewController("events").setViewName("events");
        registry.addViewController("attributes").setViewName("attributes");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(jsonMessageConverter());
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
        proxyCreator.setProxyTargetClass(true);
        return proxyCreator;
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        return messageSource;
    }

    @Bean
    public LocalAuthorizationInterceptor localAuthorizationInterceptor() {

        return new LocalAuthorizationInterceptor(configurationService.getCatalogServiceAuthToken());
    }

    public ObjectMapper objectMapper() {
        LOGGER.debug("Creating custom ObjectMapper");
        ObjectMapper mapper = JsonObjectMapperFactory.createMapper();
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter("catalogEvent", new CatalogEventFilter());
        mapper.setFilterProvider(filterProvider);
        return mapper;
    }

    public MappingJackson2HttpMessageConverter jsonMessageConverter() {
        return new MappingJackson2HttpMessageConverter(objectMapper());
    }

    @Value("${gitUserName}")
    private String gitUserName;

    @Value("${gitPassword:#{null}}")
    private String gitPassword;

    @Value("${gitPassPhrase:#{null}}")
    private String gitPassPhrase;

    @Value("${localRepoUrl:#{null}}")
    private String localRepoUrl;

    @Value("${branch:#{null}}")
    private String branch;

    @Value("${privateKeyPath:#{null}}")
    private String privateKeyPath;

    @Value("${remoteRepoUrl}")
    private String remoteRepoUrl;

    @Value("${remoteRepoCatalogPath:#{null}}")
    private String remoteRepoCatalogPath;

    @Bean
    public CatalogDao catalogDao() {
        GitCatalogDao dataSource = new GitCatalogDao();
        if (isNotBlank(gitUserName) && isNotBlank(gitPassword)) {
            dataSource.setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitUserName, gitPassword));
        }
        if (isNotBlank(remoteRepoUrl)) {
            try {
                URI uri = new URI(remoteRepoUrl);
                if (uri.getScheme().equalsIgnoreCase("SSH")) {
                    TransportConfigCallback transportConfigCallback = new TransportConfigCallback() {
                        final SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
                            @Override
                            protected JSch createDefaultJSch( FS fs ) throws JSchException {
                                JSch defaultJSch = super.createDefaultJSch( fs );
                                if (isNotBlank(privateKeyPath)) {
                                    defaultJSch.addIdentity(privateKeyPath);
                                }
                                return defaultJSch;
                            }

                            @Override
                            protected void configure(OpenSshConfig.Host host, Session session) {
                                session.setConfig("StrictHostKeyChecking", "no");
                                if (isNotBlank(gitPassPhrase)) {
                                    session.setUserInfo(new UserInfo() {
                                        @Override
                                        public String getPassphrase() {
                                            return gitPassPhrase;
                                        }

                                        @Override
                                        public String getPassword() {return null;}

                                        @Override
                                        public boolean promptPassword(String message) {return false;}

                                        @Override
                                        public boolean promptPassphrase(String message) {return true;}

                                        @Override
                                        public boolean promptYesNo(String message) {return false;}

                                        @Override
                                        public void showMessage(String message) {}
                                    });

                                }
                            }
                        };
                        @Override
                        public void configure(Transport transport) {
                            SshTransport sshTransport = ( SshTransport )transport;
                            sshTransport.setSshSessionFactory( sshSessionFactory );

                        }
                    };
                    dataSource.setTransportConfigCallback(transportConfigCallback);
                }
            } catch (URISyntaxException ex) {
                LOGGER.error("Invalid URI {}:", remoteRepoUrl, ex);
            }
        } else {
            LOGGER.error("No remote repo URL provided.");
        }
        if (isNotBlank(branch)) {
            dataSource.setBranch(branch);
        }

        if (isNotBlank(localRepoUrl)) {
            dataSource.setLocalRepoPath(localRepoUrl);
        } else {
            String localRepoPath = System.getProperty("java.io.tmpdir") + "/audit/catalog";
            File file = new File(localRepoPath);
            File parent = file.getParentFile();
            parent.mkdirs();
            dataSource.setLocalRepoPath(localRepoPath);
        }

        dataSource.setRemoteRepoUri(remoteRepoUrl);
        if (isNotBlank(remoteRepoCatalogPath)) {
            dataSource.setCatalogPath(remoteRepoCatalogPath);
        }
        return dataSource;
    }
}
