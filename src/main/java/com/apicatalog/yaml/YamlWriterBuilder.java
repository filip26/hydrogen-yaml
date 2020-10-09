package com.apicatalog.yaml;

import com.apicatalog.yaml.writer.YamlWriter;

public interface YamlWriterBuilder {
    
    YamlWriterBuilder maxLineWidth(int maximumWidth);
    
    YamlWriter build();
}