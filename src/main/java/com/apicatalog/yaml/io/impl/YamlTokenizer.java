package com.apicatalog.yaml.io.impl;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

import com.apicatalog.yaml.io.YamlLocation;

public final class YamlTokenizer implements Closeable {

    private final Reader reader;
    
    public YamlTokenizer(final Reader reader) {
        this.reader = reader;
    }
    
    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub
        
    }

    public YamlLocation getLocation() {
        // TODO Auto-generated method stub
        return null;
    }
}
