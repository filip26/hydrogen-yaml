package com.apicatalog.yaml.io.impl;

import java.io.IOException;
import java.util.NoSuchElementException;

import com.apicatalog.yaml.YamlMapping;
import com.apicatalog.yaml.YamlNode;
import com.apicatalog.yaml.YamlScalar;
import com.apicatalog.yaml.YamlSequence;
import com.apicatalog.yaml.io.YamlLocation;
import com.apicatalog.yaml.io.YamlParser;
import com.apicatalog.yaml.io.YamlParsingException;

public class YamlParserImpl implements YamlParser {

    private final YamlTokenizer tokenizer;
    
    private Event currentEvent;
    
    public YamlParserImpl(YamlTokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public boolean hasNext() throws YamlParsingException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Event next() throws YamlParsingException {
        
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public YamlLocation getLocation() {
        return tokenizer.getLocation();
    }

    @Override
    public YamlScalar getScalar() {
        // TODO Auto-generated method stub
        return null;
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
        throw new IllegalStateException(/*TODO message*/);
    }

    @Override
    public YamlMapping getMapping() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public YamlSequence getSequence() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void close() throws IOException {
        tokenizer.close();
    }
}
