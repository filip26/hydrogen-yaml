package com.apicatalog.yaml.parser.impl;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

import com.apicatalog.yaml.parser.YamlLocation;

final class YamlTokenizer implements Closeable {

    private final Reader reader;
    
    public YamlTokenizer(final Reader reader) {
        this.reader = reader;
    }
    
    @Override
    public void close() throws IOException {
        reader.close();
    }

    public YamlLocation getLocation() {
        throw new UnsupportedOperationException();
    }
}
