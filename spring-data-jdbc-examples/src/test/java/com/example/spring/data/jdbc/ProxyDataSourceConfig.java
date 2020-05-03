/*
 * Copyright 2020 Evgeniy Khyst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.spring.data.jdbc;

import static com.example.spring.data.jdbc.AbstractContainerBaseTest.POSTGRE_SQL_CONTAINER;

import javax.sql.DataSource;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@TestConfiguration
public class ProxyDataSourceConfig {

  @Bean
  public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(POSTGRE_SQL_CONTAINER.getDriverClassName());
    dataSource.setUrl(POSTGRE_SQL_CONTAINER.getJdbcUrl());
    dataSource.setUsername(POSTGRE_SQL_CONTAINER.getUsername());
    dataSource.setPassword(POSTGRE_SQL_CONTAINER.getPassword());

    return ProxyDataSourceBuilder.create(dataSource)
        .logQueryBySlf4j(SLF4JLogLevel.INFO)
        .build();
  }
}