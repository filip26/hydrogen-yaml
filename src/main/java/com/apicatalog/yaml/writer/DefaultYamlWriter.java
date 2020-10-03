package com.apicatalog.yaml.writer;

import java.util.Map;

import com.apicatalog.yaml.YamlException;
import com.apicatalog.yaml.YamlNode;
import com.apicatalog.yaml.YamlScalar;
import com.apicatalog.yaml.printer.BlockScalarType;
import com.apicatalog.yaml.printer.ChompingStyle;
import com.apicatalog.yaml.printer.FlowScalarType;
import com.apicatalog.yaml.printer.YamlPrinter;
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
                    writeKey(entry.getKey());
                    write(entry.getValue());
                }
                
                printer.endBlockdMapping();
                break;
                
            case NULL:
                printer.skip();
                break;
                
            case SCALAR:
                writeScalar(printer.getContext().iterator().next(), node.asScalar());
                break;
                
            case SEQUENCE:
                
                for (final YamlNode item : node.asSequence()) {
                    write(item);
                }

                break;
                
            }

        } catch (YamlPrinterException e) {
            throw new YamlException(e);
        }
    }
    
    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }
    
    private final void writeKey(final String key) throws YamlPrinterException {
        //TODO choose style
        printer.beginFlowScalar(FlowScalarType.PLAIN);
        printer.print(key.toCharArray());
        printer.endBlockScalar();
    }

    private final void writeScalar(final YamlPrinter.Context context, final YamlScalar scalar) throws YamlPrinterException {
        
        final char[] value = scalar.getValue().toCharArray();
        
        final int maxLength = style.getMaxLineLength() - printer.indentation();
        
        writeScalar(context, maxLength, value, 0, value.length);
        
//        switch (scalarWriter.styleScalar(context, maxLength, value, 0, value.length)) {
//        case BLOCK_LITERAL:
//            scalarWriter.printLiteral(maxLength, value, 0, value.length);   
//            break;
//            
//        case BLOCK_FOLDED:
//            printer.beginBlockScalar(BlockScalarType.FOLDED, ChompingStyle.CLIP);
//            printText(scalarWriter.getStyle().formatFolded(maxLength, value, 0, value.length), value);
//            printer.endBlockScalar();                        
//            break;
//            
//        case FLOW_PLAIN:
//            printFlowPlainScalar(value);
//            break;
//            
//        case FLOW_SINGLE_QUOTED:
//            printer.beginFlowScalar(FlowScalarType.SINGLE_QUOTED);
//            printer.print(value, 0, value.length);
//            printer.endFlowScalar();            
//            break;
//            
//        case FLOW_DOUBLE_QUOTED:
//            printer.beginFlowScalar(FlowScalarType.DOUBLE_QUOTED);
//            printer.print(value, 0, value.length);
//            printer.endFlowScalar();
//            break;
//        }        
    }
    
//    private final void printFlowPlainScalar(char[] value) throws YamlPrinterException {
//        printer.beginFlowScalar(FlowScalarType.PLAIN);
//        printer.print(value, 0, value.length);
//        printer.endFlowScalar();
//    }

    
//    private final void printText(Collection<TextPrintIndex> indices, char[] value) throws YamlPrinterException {
//        
//        for (TextPrintIndex index : indices) {
//            
//            if (index.getOffset() != -1 && index.getLength() != -1) {
//                printer.print(value, index.getOffset(), index.getLength());                    
//            }
//            
//            if (TextPrintIndex.Type.PRINT_LN.equals(index.getType())) {
//                printer.println();
//            }
//        }        
//    }
    
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
           
                if (nlCount == 0 && length < style.getMaxLineLength()) {
                    writePlain(chars, offset, length);
                    return;
                }
                
                if (nlMaxDistance <= style.getMaxLineLength()) {
                    writeLiteral(maxLineLength, chars, offset, length);

                } else {
                    writeFolded(maxLineLength, chars, offset, length);
                }
                return;
            }
            
            if (includesControl) {
                
            }
            
        }
        
        // TODO Auto-generated method stub
        writeDoubleQuoted(maxLineLength, chars, offset, length);
    }
    
    private static final boolean isDocumentContext(YamlPrinter.Context context) {
        return YamlPrinter.Context.DOCUMENT_BEGIN.equals(context)
                || YamlPrinter.Context.DOCUMENT_END.equals(context);
    }
          
    private void writeLiteral(int maxLineLength, char[] chars, int offset, int length) throws YamlPrinterException {

        printer.beginBlockScalar(BlockScalarType.LITERAL, ChompingStyle.CLIP);
        
        int begin = 0;
        
        for (int i = 0; i < length; i++) {
            
            if ('\n' == chars[i + offset]) {
                printer.print(chars, offset + begin, i - begin);
                printer.println();
                begin = i + 1;
            }
            
        }
        
        if (begin < length) {
            printer.print(chars, offset + begin, length - begin);
        }        
        
        printer.endBlockScalar();
    }

    private void writePlain(char[] chars, int offset, int length) throws YamlPrinterException {
        
    }

    private void writeDoubleQuoted(int maxLineLength, char[] chars, int offset, int length) throws YamlPrinterException {
        
    }

    private void writeFolded(int maxLineLength, char[] chars, int offset, int length) throws YamlPrinterException {
        
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
