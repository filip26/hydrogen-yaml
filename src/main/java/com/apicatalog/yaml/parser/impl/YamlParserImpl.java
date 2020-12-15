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
package com.apicatalog.yaml.parser.impl;

import java.io.IOException;
import java.util.NoSuchElementException;

import com.apicatalog.yaml.node.YamlMapping;
import com.apicatalog.yaml.node.YamlNode;
import com.apicatalog.yaml.node.YamlScalar;
import com.apicatalog.yaml.node.YamlSequence;
import com.apicatalog.yaml.parser.YamlLocation;
import com.apicatalog.yaml.parser.YamlParser;
import com.apicatalog.yaml.parser.YamlParsingException;

class YamlParserImpl implements YamlParser {

    private final YamlTokenizer tokenizer;
    
    private Event currentEvent;
    
    public YamlParserImpl(YamlTokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public boolean hasNext() throws YamlParsingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Event next() throws YamlParsingException {
        
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        
        throw new UnsupportedOperationException();
    }

    @Override
    public YamlLocation getLocation() {
        return tokenizer.getLocation();
    }

    @Override
    public YamlScalar getScalar() {
        throw new UnsupportedOperationException();
    }

    @Override
    public YamlNode getNode() {
        
        switch (currentEvent) {
        case START_MAPPING:
            break;
            
        case START_SEQUENCE:
            break;
            
        case SCALAR:
            break;
            
        default:
        }
        
        throw new IllegalStateException();
    }

    @Override
    public YamlMapping getMapping() {
        throw new UnsupportedOperationException();
    }

    @Override
    public YamlSequence getSequence() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
        tokenizer.close();
    }
}
