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
package org.urlshortener.util;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Evgeniy Khist
 */
public class SimpleUrlValidatorTest {
    
    @Test
    public void testIsValid() {
        SimpleUrlValidator urlValidator = new SimpleUrlValidator();
        
        assertTrue(urlValidator.isValid("http://openshift.com"));
        assertFalse(urlValidator.isValid("openshift.com"));
        assertFalse(urlValidator.isValid("www.openshift.com"));
        assertTrue(urlValidator.isValid("https://openshift.com"));
        assertTrue(urlValidator.isValid("https://www.openshift.com"));
        assertFalse(urlValidator.isValid("somestring"));
        assertFalse(urlValidator.isValid("1"));
        assertFalse(urlValidator.isValid("ftp://example.com"));
    }
}
