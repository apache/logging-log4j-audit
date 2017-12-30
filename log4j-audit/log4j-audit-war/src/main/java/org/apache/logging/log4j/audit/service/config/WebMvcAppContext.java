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
package org.apache.logging.log4j.audit.service.config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.audit.AuditLogger;
import org.apache.logging.log4j.audit.catalog.CatalogManager;
import org.apache.logging.log4j.audit.service.catalog.AuditCatalogManager;
import org.apache.logging.log4j.audit.service.catalog.AuditManager;
import org.apache.logging.log4j.audit.service.security.LocalAuthorizationInterceptor;
import org.apache.logging.log4j.audit.util.JsonObjectMapperFactory;
import org.apache.logging.log4j.catalog.api.dao.CatalogDao;
import org.apache.logging.log4j.catalog.api.CatalogReader;
import org.apache.logging.log4j.catalog.api.dao.ClassPathCatalogReader;
import org.apache.logging.log4j.catalog.api.util.CatalogEventFilter;
import org.apache.logging.log4j.catalog.git.dao.GitCatalogDao;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FS;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


@Configuration
@EnableWebMvc
@EnableScheduling
@ComponentScan(basePackages = {"org.apache.logging.log4j.catalog.jpa", "org.apache.logging.log4j.audit.service"})
//@ImportResource("classpath*:propertySources.xml")
public class WebMvcAppContext extends WebMvcConfigurerAdapter {

    private static final Logger LOGGER = LogManager.getLogger(WebMvcAppContext.class);

    @Autowired
    ConfigurationService configurationService;

    @Value("${gitUserName")
    private String gitUserName;

    @Value("${gitPassword:#{null}}")
    private String gitPassword;

    @Value("${gitPassPhrase:#{null}}")
    private String gitPassPhrase;

    @Value("${gitLocalRepoPath:#{null}}")
    private String localRepoUrl;

    @Value("${privateKeyPath:#{null}}")
    private String privateKeyPath;

    @Value("${getRemoteRepoUri}")
    private String remoteRepoUrl;

    @Value("${remoteRepoCatalogPath:#{null}}")
    private String remoteRepoCatalogPath;

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(jsonMessageConverter());
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localAuthorizationInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/swagger**")
                .excludePathPatterns("/v2/api-docs**")
                .excludePathPatterns("/configuration/security**")
                .excludePathPatterns("/configuration/ui**")
                .excludePathPatterns("/webjars/**");
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
        proxyCreator.setProxyTargetClass(true);
        return proxyCreator;
    }

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        return messageSource;
    }

    @Bean
    public LocalAuthorizationInterceptor localAuthorizationInterceptor() {

        return new LocalAuthorizationInterceptor(configurationService.getAuditServiceAuthToken());
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = JsonObjectMapperFactory.createMapper();
        SimpleFilterProvider filterProvider = new SimpleFilterProvider();
        filterProvider.addFilter("catalogEvent", new CatalogEventFilter());
        mapper.setFilterProvider(filterProvider);
        return mapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter jsonMessageConverter() {
        return new MappingJackson2HttpMessageConverter(objectMapper());
    }

    @Bean
    public List<ClientHttpRequestInterceptor> restInterceptors() {
        return Arrays.asList(new ClientHttpRequestInterceptor[] {});
    }

    @Bean
    public CatalogReader catalogReader() {
        try {
            return new ClassPathCatalogReader();
        } catch (IOException ioe) {
            LOGGER.error("Unable to create ClassPathCatalogReader", ioe);
            return null;
        }
    }

    @Bean
    public AuditManager auditManager() {
        return new AuditCatalogManager(catalogReader());
    }

    @Bean
    AuditLogger auditLogger() {
        AuditLogger auditLogger = new AuditLogger();
        auditLogger.setCatalogManager(auditManager());
        return auditLogger;
    }

}
