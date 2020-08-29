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
package com.apicatalog.yaml;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;

import com.apicatalog.yaml.io.YamlParser;

public final class Yaml {

    
    public static final YamlParser createParser(InputStream input) {
        //TODO
        return null;
    }

    
    public static final YamlParser createParser(Reader reader) {
        //TODO
        return null;
    }
    
    public static final YamlWriter createWriter(OutputStream output) {
        //TODO
        return null;
    }
    
    public static final YamlWriter createWriter(Writer writer) {
        //TODO
        return null;
    }
    
    public static final YamlMappingBuilder createMappingBuilder() {
        //TODO
        return null;        
    }

    public static final YamlMappingBuilder createMappingBuilder(YamlMapping mapping) {
        //TODO
        return null;        
    }

    public static final YamlSequenceBuilder createSequenceBuilder() {
        //TODO
        return null;
    }
    
    public static final YamlSequenceBuilder createSequenceBuilder(YamlSequence sequence) {
        //TODO
        return null;
    }
    
    public static final YamlScalar createScalar(String value) {
        //TODO
        return null;        
    }

    public static final YamlScalar createScalar(Collection<String> lines) {
        //TODO
        return null;        
    }

}
