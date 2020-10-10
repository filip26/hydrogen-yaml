package com.apicatalog.yaml.parser.impl;

import java.io.IOException;
import java.util.NoSuchElementException;

import com.apicatalog.yaml.YamlMapping;
import com.apicatalog.yaml.YamlNode;
import com.apicatalog.yaml.YamlScalar;
import com.apicatalog.yaml.YamlSequence;
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
