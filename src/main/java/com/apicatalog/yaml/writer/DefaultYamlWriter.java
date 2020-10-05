package com.apicatalog.yaml.writer;

import java.io.IOException;
import java.util.Map;

import com.apicatalog.yaml.YamlException;
import com.apicatalog.yaml.YamlNode;
import com.apicatalog.yaml.YamlScalar;
import com.apicatalog.yaml.printer.BlockScalarType;
import com.apicatalog.yaml.printer.ChompingStyle;
import com.apicatalog.yaml.printer.YamlPrinter;
import com.apicatalog.yaml.printer.YamlPrinter.Context;
import com.apicatalog.yaml.printer.YamlPrinterException;

public class DefaultYamlWriter implements YamlWriter {

    private final YamlPrinter printer;
    private final YamlPrintStyle style;
    
    public DefaultYamlWriter(YamlPrinter printer, YamlPrintStyle style) {
        this.printer  = printer;
        this.style = style;
    }
    
    @Override
    public void write(final YamlNode node) throws YamlException {

        try {
        
            switch (node.getNodeType()) {
            case MAPPING:
                printer.beginBlockMapping();
                
                for (final Map.Entry<String, YamlNode> entry : node.asMapping().entrySet()) {
                    writeScalar(Context.BLOCK_MAPPING_KEY, entry.getKey());
                    write(entry.getValue());
                }
                
                printer.endBlockdMapping();
                break;
                
            case NULL:
                printer.printNull();
                break;
                
            case SCALAR:
                writeScalar(printer.getContext().iterator().next(), node.asScalar());
                break;
                
            case SEQUENCE:
                
                printer.beginBlockSequence();
                
                for (final YamlNode item : node.asSequence()) {
                    write(item);
                }

                printer.endBlockSequence();
                break;
                
            }

        } catch (YamlPrinterException e) {
            throw new YamlException(e);
        }
    }
    
    @Override
    public void close() throws IOException {
        printer.close();
    }
    
    private final void writeScalar(final YamlPrinter.Context context, String scalar) throws YamlPrinterException {

        final char[] value = scalar.toCharArray();
        
        final int maxLength = style.getMaxLineLength() - printer.indentation();
        
        writeScalar(context, maxLength, value, 0, value.length);

    }
    
    private final void writeScalar(final YamlPrinter.Context context, final YamlScalar scalar) throws YamlPrinterException {        
        writeScalar(context, scalar.getValue());
    }

    private final void writeScalar(YamlPrinter.Context context, int maxLineLength, char[] chars, int offset, int length) throws YamlPrinterException {
        
        boolean allPrintable = true;
        boolean includesControl = false;
        
        int nlCount = 0;
        
        int nlLastIndex = 0;
        int nlMaxDistance = -1;
        
        for (int i=0; i < length; i++) {
            
            char ch = chars[i + offset];
            
            if (allPrintable) {
                allPrintable = YamlCharacters.IS_PRINTABLE.test(ch); 
            }
            
            if (!includesControl) {
                includesControl = YamlCharacters.IS_CONTROL.test(ch);
            }
            
            if ('\n' == ch) {
                nlCount++;
                
                nlMaxDistance = Math.max(nlMaxDistance, i - nlLastIndex);
                nlLastIndex = i;
            }
        }

        if (isDocumentContext(context)) {
            
            if (allPrintable) {
           
                if (nlCount == 0 && !includesControl && length < maxLineLength) {
                    writePlain(chars, offset, length);
                    return;
                }
                
                if (nlCount == 0 && includesControl && length < maxLineLength) {
                    writeSingleQuoted(maxLineLength, chars, offset, length);
                    return;
                }
                                
                if (nlMaxDistance <= maxLineLength) {
                    if (!includesControl) {
                        writePlain(chars, offset, length);
                        return;
                    }
                    writeLiteral(maxLineLength, chars, offset, length);

                } else {
                    writeFolded(maxLineLength, chars, offset, length);
                }
                
                return;
            }
        }

        if (allPrintable && !includesControl && nlCount == 0 && (length < maxLineLength || YamlPrinter.Context.BLOCK_MAPPING_KEY.equals(context))) {
            writePlain(chars, offset, length);
            return;
        }
        
        if (allPrintable && includesControl && nlCount == 0 && (length < maxLineLength || YamlPrinter.Context.BLOCK_MAPPING_KEY.equals(context))) {
            writeSingleQuoted(maxLineLength, chars, offset, length);
            return;
        }
        
        if (allPrintable && !YamlPrinter.Context.BLOCK_MAPPING_KEY.equals(context)) {

            if (nlMaxDistance <= maxLineLength) {
                writeLiteral(maxLineLength, chars, offset, length);

            } else {
                writeFolded(maxLineLength, chars, offset, length);
            }
            return;            
        }

        writeDoubleQuoted(!YamlPrinter.Context.BLOCK_MAPPING_KEY.equals(context), maxLineLength, chars, offset, length);
    }
    
    private static final boolean isDocumentContext(YamlPrinter.Context context) {
        return YamlPrinter.Context.DOCUMENT_BEGIN.equals(context)
                || YamlPrinter.Context.DOCUMENT_END.equals(context);
    }
          
    private void writeLiteral(int maxLineLength, char[] chars, int offset, int length) throws YamlPrinterException {

        printer.beginBlockScalar(BlockScalarType.LITERAL, ChompingStyle.CLIP);
        
        int begin = 0;
        boolean empty = true;
        
        for (int i = 0; i < length; i++) {
            
            if ('\n' == chars[i + offset]) {
                if (!empty) {
                    printer.print(chars, offset + begin, i - begin);
                    printer.println();
                }
                begin = i + 1;
            }
            
            empty = empty && (' ' == chars[i + offset]);
        }
        
        if (begin < length) {
            printer.print(chars, offset + begin, length - begin);
        }        
        
        printer.endScalar();
    }

    private void writePlain(char[] chars, int offset, int length) throws YamlPrinterException {
        printer.beginPlainScalar();
        printer.print(chars, offset, length);
        printer.endScalar();
    }

    private void writeSingleQuoted(int maxLineLength, char[] chars, int offset, int length) throws YamlPrinterException {
        printer.beginSingleQuotedScalar();
        writeFoldedText(maxLineLength, chars, offset, length);
        printer.endScalar();
    }
    
    private void writeDoubleQuoted(boolean multiline, int maxLineLength, char[] chars, int offset, int length) throws YamlPrinterException {
        printer.beginDoubleQuotedScalar();

        if (multiline) {
            writeFoldedText(maxLineLength, chars, offset, length);
        } else {
            printer.print(chars, offset, length);
        }
        printer.endScalar();
    }

    private void writeFolded(int maxLineLength, char[] chars, int offset, int length) throws YamlPrinterException {
        
        printer.beginBlockScalar(BlockScalarType.FOLDED, ChompingStyle.CLIP);
        writeFoldedText(maxLineLength, chars, offset, length);
        printer.endScalar();
    }

    private void writeFoldedText(int maxLineLength, char[] chars, int offset, int length) throws YamlPrinterException {

        int lineIndex = 0;
        int lastSpaceIndex = 0;
        
        for (int i = 0; i < length; i++) {
            
            if ('\n' == chars[i + offset]) {
                printer.print(chars, offset + lineIndex, i - lineIndex);
                printer.println();
                printer.println();
                lineIndex = i + 1;
                lastSpaceIndex = i + 1;
                
            } else if (i - lineIndex >=  maxLineLength) {       
                printer.print(chars, offset + lineIndex, lastSpaceIndex - lineIndex - 1);
                printer.println();
                lineIndex = lastSpaceIndex;                
                
            } else if (' ' == chars[i + offset]) {
                
                lastSpaceIndex = i + 1;                                    
            }
        }
        
        if (lineIndex < length) {
            printer.print(chars, offset + lineIndex, length - lineIndex);
        }
    }
}
