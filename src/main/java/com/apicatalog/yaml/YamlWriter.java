package com.apicatalog.yaml;

import java.io.Closeable;

public interface YamlWriter extends Closeable {

    void write(YamlNode node);
    
    
}
