package com.apicatalog.yaml.printer.style;

import java.util.Collection;

public interface YamlPrinterStyle {

    enum Context {
        DOCUMENT,
        BLOCK_MAPPING_KEY,
        BLOCK_MAPPING_VALUE,
        SEQUENCE
    }
    
    ScalarStyle scalarStyle(Context context, int maxLineLength, char[] chars, int offset, int length);
    
    Collection<TextPrintIndex> formatBlockLiteral(int maxLineLength, char[] chars, int offset, int length);
        
    
}
