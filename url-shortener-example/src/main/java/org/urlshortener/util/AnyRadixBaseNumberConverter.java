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

import javax.inject.Inject;
import org.urlshortener.config.Property;

/**
 *
 * @author Evgeniy Khist
 */
public class AnyRadixBaseNumberConverter implements NumberConverter {

    private final String alphabeth;
    private final int alphabethLength;

    @Inject
    public AnyRadixBaseNumberConverter(@Property("shortened.url.alphabeth") String alphabeth) {
        this.alphabeth = alphabeth;
        this.alphabethLength = alphabeth.length();
    }

    @Override
    public String convertToAlphabeth(long number) {
        String result = "";
        long tmp = number;
        while (tmp != 0) {
            long module = tmp % alphabethLength;
            result = alphabeth.charAt((int) module) + result;
            tmp /= alphabethLength;
        }
        return result;
    }
    
    @Override
    public long convertFromAlphabeth(String number) {
        long result = 0;
        int power = 0;
        for (int i = number.length() - 1; i >= 0; i--) {
            int mantissa = alphabeth.indexOf(number.charAt(i));
            result += mantissa * Math.pow(alphabethLength, power++);
        }
        return result;
    }
}
