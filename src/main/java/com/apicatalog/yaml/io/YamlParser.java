/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.apicatalog.yaml.io;

import java.io.Closeable;
import java.io.IOException;

import com.apicatalog.yaml.YamlMapping;
import com.apicatalog.yaml.YamlNode;
import com.apicatalog.yaml.YamlScalar;
import com.apicatalog.yaml.YamlSequence;

public interface YamlParser extends Closeable {

    enum Event {
        START_DOCUMENT,
        END_DOCUMENT,
        START_MAPPING,
        END_MAPPING,
        START_SEQUENCE,
        END_SEQUENCE,
        SCALAR,
    }

    boolean hasNext() throws YamlParsingException;
    
    Event next() throws YamlParsingException;
    
    YamlLocation getLocation();
    
    YamlScalar getScalar();
    
    YamlNode getNode();
    
    YamlMapping getMapping();
    
    YamlSequence getSequence();
    
    @Override
    void close() throws IOException;
}
