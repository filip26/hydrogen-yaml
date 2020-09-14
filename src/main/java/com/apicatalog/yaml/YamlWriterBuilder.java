package com.apicatalog.yaml;

import com.apicatalog.yaml.writer.YamlWriter;

public interface YamlWriterBuilder {
    
    YamlWriterBuilder maxiumWidth(int maximumWidth);
    
    YamlWriter build();
}