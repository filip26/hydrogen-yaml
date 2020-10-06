package com.apicatalog.yaml.writer;

import java.io.IOException;
import java.util.Map;

import com.apicatalog.yaml.YamlException;
import com.apicatalog.yaml.YamlNode;
import com.apicatalog.yaml.printer.YamlPrinter;
import com.apicatalog.yaml.printer.YamlPrinterException;

public class DefaultYamlWriter implements YamlWriter {

    private final YamlPrinter printer;
    
    public DefaultYamlWriter(YamlPrinter printer) {
        this.printer  = printer;
    }
    
    @Override
    public void write(final YamlNode node) throws YamlException, IOException {

        try {
        
            switch (node.getNodeType()) {
            case MAPPING:
                printer.beginBlockMapping();
                
                for (final Map.Entry<String, YamlNode> entry : node.asMapping().entrySet()) {
                    writeScalar(entry.getKey());
                    write(entry.getValue());
                }
                
                printer.endBlockdMapping();
                break;
                
            case NULL:
                printer.printNull();
                break;
                
            case SCALAR:
                writeScalar(node.asScalar().getValue());
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
    
    private final void writeScalar(String scalar) throws YamlPrinterException, IOException {

        final char[] value = scalar.toCharArray();
        
        printer.printScalar(value, 0, value.length);
    }
//        
//        
//        final int maxLength = style.getMaxLineLength() - printer.indentation();
//        
//        writeScalar(context, maxLength, value, 0, value.length);
//    private void writeLiteral(int maxLineLength, char[] chars, int offset, int length) throws YamlPrinterException {
//
//        printer.printLiteralScalar(BlockScalarType.LITERAL, ChompingStyle.CLIP);
//        
//        int begin = 0;
//        boolean empty = true;
//        
//        for (int i = 0; i < length; i++) {
//            
//            if ('\n' == chars[i + offset]) {
//                if (!empty) {
//                    printer.print(chars, offset + begin, i - begin);
//                    printer.println();
//                }
//                begin = i + 1;
//            }
//            
//            empty = empty && (' ' == chars[i + offset]);
//        }
//        
//        if (begin < length) {
//            printer.print(chars, offset + begin, length - begin);
//        }        
//        
//        printer.endScalar();
//    }
//
//    private void writePlain(char[] chars, int offset, int length) throws YamlPrinterException {
//        printer.printPlainScalar();
//        printer.print(chars, offset, length);
//        printer.endScalar();
//    }
//
//    private void writeSingleQuoted(int maxLineLength, char[] chars, int offset, int length) throws YamlPrinterException {
//        printer.printSingleQuotedScalar();
//        writeFoldedText(maxLineLength, chars, offset, length);
//        printer.endScalar();
//    }
//    
//    private void writeDoubleQuoted(boolean multiline, int maxLineLength, char[] chars, int offset, int length) throws YamlPrinterException {
//        printer.printDoubleQuotedScalar();
//
//        if (multiline) {
//            writeFoldedText(maxLineLength, chars, offset, length);
//        } else {
//            printer.print(chars, offset, length);
//        }
//        printer.endScalar();
//    }
//
//    private void writeFolded(int maxLineLength, char[] chars, int offset, int length) throws YamlPrinterException {
//        
//        printer.printLiteralScalar(BlockScalarType.FOLDED, ChompingStyle.CLIP);
//        writeFoldedText(maxLineLength, chars, offset, length);
//        printer.endScalar();
//    }
//
//    private void writeFoldedText(int maxLineLength, char[] chars, int offset, int length) throws YamlPrinterException {
//
//        int lineIndex = 0;
//        int lastSpaceIndex = 0;
//        
//        for (int i = 0; i < length; i++) {
//            
//            if ('\n' == chars[i + offset]) {
//                printer.print(chars, offset + lineIndex, i - lineIndex);
//                printer.println();
//                printer.println();
//                lineIndex = i + 1;
//                lastSpaceIndex = i + 1;
//                
//            } else if (i - lineIndex >=  maxLineLength) {       
//                printer.print(chars, offset + lineIndex, lastSpaceIndex - lineIndex - 1);
//                printer.println();
//                lineIndex = lastSpaceIndex;                
//                
//            } else if (' ' == chars[i + offset]) {
//                
//                lastSpaceIndex = i + 1;                                    
//            }
//        }
//        
//        if (lineIndex < length) {
//            printer.print(chars, offset + lineIndex, length - lineIndex);
//        }
//    }
}
