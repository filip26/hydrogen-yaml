package com.apicatalog.yaml.printer.style;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

import com.apicatalog.yaml.printer.style.TextPrintIndex.Type;

public final class DefaultYamlPrinterStyle implements YamlPrinterStyle {

    public static final YamlPrinterStyle INSTANCE = new DefaultYamlPrinterStyle();
    
    @Override
    public ScalarStyle scalarStyle(YamlPrinterStyle.Context context,  int maxLineLength, char[] chars, int offset, int length) {
        
        boolean allPrintable = true;
        int nlCount = 0;
        
        for (int i=0; i < length; i++) {
            
            char ch = chars[i + offset];
            
            if (allPrintable) {
                allPrintable = IS_PRINTABLE.test(ch); 
            }
            if ('\n' == ch) {
                nlCount++;
            }
        }

        if (YamlPrinterStyle.Context.DOCUMENT.equals(context)) {
            
            if (allPrintable) {
           
                if (nlCount == 0) {
                    return ScalarStyle.FLOW_PLAIN;
                }
                
                return ScalarStyle.BLOCK_LITERAL;
            }
            
        }
        
        // TODO Auto-generated method stub
        return ScalarStyle.FLOW_DOUBLE_QUOTED;
    }

    private static final Predicate<Character> IS_PRINTABLE = ch ->
                                                                ch == 0x9 || ch == 0xA || ch == 0xD
                                                                || (ch >= 0x20 && ch <= 0x7E)
                                                                || ch == 0x85
                                                                || (ch >= 0xA0 && ch <= 0xD7FF)
                                                                || (ch >= 0xE000 && ch <= 0xFFFD)
                                                                ;

    @Override
    public Collection<TextPrintIndex> formatBlockLiteral(int maxLineLength, char[] chars, int offset, int length) {

        final ArrayList<TextPrintIndex> indicies = new ArrayList<>(); 
        
        int begin = 0;
        
        for (int i = 0; i < length; i++) {
            
            if ('\n' == chars[i + offset]) {
                indicies.add(new TextPrintIndex(Type.PRINT_LN, offset + begin, i - begin));
                begin = i + 1;
            }
            
        }
        
        if (begin < length) {
            indicies.add(new TextPrintIndex(Type.PRINT, offset + begin, length - begin));
        }
        
        return indicies;
    }
}
