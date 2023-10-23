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
package org.apache.logging.log4j.catalog.jpa.config;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.DriverManagerDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.catalog.api.annotation.JdbcUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure using Postgres as the database
 */
@Configuration
@JdbcUrl("postgresql")
public class PostgresqlDataSourceConfig implements DataSourceConfig {

    private static final Logger LOGGER = LogManager.getLogger(PostgresqlDataSourceConfig.class);

    @Value("${jdbcUrl}")
    private String url;

    @Value("${dbUserName}")
    private String userName;

    @Value("${dbPassword}")
    private String password;

    @Bean
    public DataSource dataSource() {
        LOGGER.debug("Creating PostgresQL data source for {}", url);
        DriverManagerDataSource driver = new DriverManagerDataSource();
        driver.setDriverClass("org.postgresql.Driver");
        driver.setJdbcUrl(url);
        driver.setUser(userName);
        driver.setPassword(password);
        return driver;
    }
}
