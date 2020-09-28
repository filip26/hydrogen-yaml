package com.apicatalog.yaml.writer.style;

public interface YamlStyle {

    enum Context {
        DOCUMENT,
        BLOCK_MAPPING_KEY,
        BLOCK_MAPPING_VALUE,
        SEQUENCE
    }
    
    ScalarStyle scalar(Context context, char[] chars, int offset, int length);
    
    
    
}
