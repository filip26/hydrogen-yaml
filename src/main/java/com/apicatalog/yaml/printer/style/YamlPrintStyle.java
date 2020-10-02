package com.apicatalog.yaml.printer.style;

import java.util.Collection;

public interface YamlPrintStyle {

    enum Context {
        DOCUMENT,
        BLOCK_MAPPING_KEY,
        BLOCK_MAPPING_VALUE,
        SEQUENCE
    }
    
    ScalarStyle styleScalar(Context context, int maxLineLength, char[] chars, int offset, int length);
    
    Collection<TextPrintIndex> formatLiteral(int maxLineLength, char[] chars, int offset, int length);
    
    Collection<TextPrintIndex> formatFolded(int maxLineLength, char[] chars, int offset, int length);
    
}
