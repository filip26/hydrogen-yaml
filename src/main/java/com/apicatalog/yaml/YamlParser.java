package com.apicatalog.yaml;

import java.io.Closeable;

public interface YamlParser extends Closeable {

    YamlNode read();
    
}
