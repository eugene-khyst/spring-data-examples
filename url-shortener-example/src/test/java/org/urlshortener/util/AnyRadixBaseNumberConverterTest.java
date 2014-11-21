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

import org.urlshortener.util.AnyRadixBaseNumberConverter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Evgeniy Khist
 */
public class AnyRadixBaseNumberConverterTest {
    
    @Test
    public void testConvert() {
        AnyRadixBaseNumberConverter converter;
        
        converter = new AnyRadixBaseNumberConverter("0123456789");
        assertEquals("1234", converter.convertToAlphabeth(1234L));
        assertEquals(1234L, converter.convertFromAlphabeth("1234"));
        
        converter = new AnyRadixBaseNumberConverter("0123456789abcdefghijklmnopqrstuvwxyz");
        assertEquals("ya", converter.convertToAlphabeth(1234));
        assertEquals(1234L, converter.convertFromAlphabeth("ya"));
    }
}
