package com.apicatalog.yaml.io.impl;

import java.io.Reader;

import com.apicatalog.yaml.YamlMapping;
import com.apicatalog.yaml.YamlNode;
import com.apicatalog.yaml.YamlScalar;
import com.apicatalog.yaml.YamlSequence;
import com.apicatalog.yaml.io.YamlLocation;
import com.apicatalog.yaml.io.YamlParser;
import com.apicatalog.yaml.io.YamlParsingException;

public class YamlParserImpl implements YamlParser {

    private final YamlTokenizer tokenizer; 
    
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public YamlLocation getLocation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public YamlScalar getScalar() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public YamlNode getNode() {
        // TODO Auto-generated method stub
        return null;
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
    public void close() {
        // TODO Auto-generated method stub
        
    }
}
