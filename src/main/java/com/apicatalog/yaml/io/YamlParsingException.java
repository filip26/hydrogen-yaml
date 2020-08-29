package com.apicatalog.yaml.io;

import com.apicatalog.yaml.YamlException;

public class YamlParsingException extends YamlException {

    private static final long serialVersionUID = 706265442300521912L;
    
    private final YamlLocation location;

    public YamlParsingException(String message, YamlLocation location) {
        super(message);
        this.location = location;
    }

    public YamlParsingException(String message, Throwable cause, YamlLocation location) {
        super(message, cause);
        this.location = location;
    }
    
    public YamlLocation getLocation() {
        return location;
    }
}
