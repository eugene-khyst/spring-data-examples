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

package com.example.spring.data.jpa;

import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.testcontainers.containers.PostgreSQLContainer;

@Slf4j
abstract class AbstractContainerBaseTest {

  static final PostgreSQLContainer POSTGRE_SQL_CONTAINER;

  static {
    POSTGRE_SQL_CONTAINER = new PostgreSQLContainer();
    POSTGRE_SQL_CONTAINER.start();
    Runtime.getRuntime().addShutdownHook(new Thread(POSTGRE_SQL_CONTAINER::stop));
  }

  @BeforeEach
  void logTestInfo(TestInfo testInfo) {
    log.info("{}.{}",
        testInfo.getTestClass().map(Class::getSimpleName).orElse("N/A"),
        testInfo.getTestMethod().map(Method::getName).orElse("N/A"));
  }
}
