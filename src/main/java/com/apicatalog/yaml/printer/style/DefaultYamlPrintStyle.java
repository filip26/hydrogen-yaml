package com.apicatalog.yaml.printer.style;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

import com.apicatalog.yaml.printer.style.TextPrintIndex.Type;

public final class DefaultYamlPrintStyle implements YamlPrintStyle {

    public static final YamlPrintStyle INSTANCE = new DefaultYamlPrintStyle();
    
    @Override
    public ScalarStyle styleScalar(YamlPrintStyle.Context context,  int maxLineLength, char[] chars, int offset, int length) {
        
        boolean allPrintable = true;
        int nlCount = 0;
        
        int nlLastIndex = 0;
        int nlMaxDistance = -1;
        
        for (int i=0; i < length; i++) {
            
            char ch = chars[i + offset];
            
            if (allPrintable) {
                allPrintable = IS_PRINTABLE.test(ch); 
            }
            if ('\n' == ch) {
                nlCount++;
                
                nlMaxDistance = Math.max(nlMaxDistance, i - nlLastIndex);
                nlLastIndex = i;
            }
        }

        if (YamlPrintStyle.Context.DOCUMENT.equals(context)) {
            
            if (allPrintable) {
           
                if (nlCount == 0 && length < maxLineLength) {
                    return  ScalarStyle.FLOW_PLAIN;
                }
                
                return (nlMaxDistance <= maxLineLength) ? ScalarStyle.BLOCK_LITERAL : ScalarStyle.BLOCK_FOLDED;
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
    public Collection<TextPrintIndex> formatLiteral(int maxLineLength, char[] chars, int offset, int length) {

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

    @Override
    public Collection<TextPrintIndex> formatFolded(int maxLineLength, char[] chars, int offset, int length) {
        final ArrayList<TextPrintIndex> indicies = new ArrayList<>(); 
        
        int lineIndex = 0;
        int lastSpaceIndex = 0;
        
        for (int i = 0; i < length; i++) {
            
            if ('\n' == chars[i + offset]) {
                indicies.add(new TextPrintIndex(Type.PRINT_LN, offset + lineIndex, i - lineIndex));
                indicies.add(TextPrintIndex.NEW_LINE);
                lineIndex = i + 1;
                lastSpaceIndex = i + 1;
                
            } else if (i - lineIndex >=  maxLineLength) {
                
                indicies.add(new TextPrintIndex(Type.PRINT_LN, offset + lineIndex, lastSpaceIndex - lineIndex - 1));
                lineIndex = lastSpaceIndex;                
                
            } else if (' ' == chars[i + offset]) {
                
                lastSpaceIndex = i + 1;                                    
            }
            
        }
        
        if (lineIndex < length) {
            indicies.add(new TextPrintIndex(Type.PRINT, offset + lineIndex, length - lineIndex));
        }
        
        return indicies;
    }
}
