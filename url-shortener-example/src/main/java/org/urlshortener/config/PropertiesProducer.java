/*
 * Copyright 2014 Evgeniy Khist.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.urlshortener.config;

import java.util.Properties;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

/**
 *
 * @author Evgeniy Khist
 */
public class PropertiesProducer {

    private final PropertiesSource propertiesSource;

    @Inject
    public PropertiesProducer(PropertiesSource propertiesSource) {
        this.propertiesSource = propertiesSource;
    }
    
    @Produces
    @Property
    public String getStringProperty(InjectionPoint ip) {
        Property property = ip.getAnnotated().getAnnotation(Property.class);
        Properties properties = propertiesSource.getProperties();
        return properties.getProperty(property.value(), property.defaultValue());
    }
}
