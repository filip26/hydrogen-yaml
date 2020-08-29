package com.apicatalog.yaml;

public class YamlException extends Exception {

    private static final long serialVersionUID = -5661155688159239871L;

    public YamlException(String message) {
        super(message);
    }
    
    public YamlException(String message, Throwable cause) {
        super(message, cause);
    }
}
