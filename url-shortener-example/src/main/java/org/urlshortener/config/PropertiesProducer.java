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

    @Inject
    private PropertiesSource propertiesSource;

    @Produces
    @Property
    public String getStringProperty(InjectionPoint ip) {
        Property property = ip.getAnnotated().getAnnotation(Property.class);
        Properties properties = propertiesSource.getProperties();
        if (property.required() && !properties.containsKey(property.value())) {
            throw new RuntimeException("Could not find property " + property.value());
        }
        String result = properties.getProperty(property.value(), property.defaultValue());
        return result;
    }
    
    @Produces
    @Property
    public Integer getIntegerProperty(InjectionPoint ip) {
        String stringProperty = getStringProperty(ip);
        return stringProperty != null ? Integer.parseInt(stringProperty) : null;
    }
    
    @Produces
    @Property
    public Long getLongProperty(InjectionPoint ip) {
        String stringProperty = getStringProperty(ip);
        return stringProperty != null ? Long.parseLong(stringProperty) : null;
    }
}
