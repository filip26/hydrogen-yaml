package com.apicatalog.yaml.printer.style;

public interface YamlPrinterStyle {

    enum Context {
        DOCUMENT,
        BLOCK_MAPPING_KEY,
        BLOCK_MAPPING_VALUE,
        SEQUENCE
    }
    
    ScalarStyle scalar(Context context, int maxLineLength, char[] chars, int offset, int length);
    
    
    
}
