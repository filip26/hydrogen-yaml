package com.apicatalog.yaml.writer;

import java.util.Collection;
import java.util.Map;

import com.apicatalog.yaml.YamlException;
import com.apicatalog.yaml.YamlNode;
import com.apicatalog.yaml.YamlScalar;
import com.apicatalog.yaml.printer.BlockScalarType;
import com.apicatalog.yaml.printer.ChompingStyle;
import com.apicatalog.yaml.printer.FlowScalarType;
import com.apicatalog.yaml.printer.YamlPrinter;
import com.apicatalog.yaml.printer.YamlPrinterException;
import com.apicatalog.yaml.printer.style.TextPrintIndex;
import com.apicatalog.yaml.printer.style.YamlPrintStyle;

public class YamlWriterImpl implements YamlWriter {

    private final YamlPrinter printer;
    private final YamlWriterOptions options;
    
    public YamlWriterImpl(YamlPrinter printer, YamlWriterOptions options) {
        this.printer  = printer;
        this.options = options;
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
                writeScalar(toStyleContext(printer.getContext().iterator().next()), node.asScalar());
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

    private final void writeScalar(final YamlPrintStyle.Context context, final YamlScalar scalar) throws YamlPrinterException {
        
        final char[] value = scalar.getValue().toCharArray();
        
        final int maxLength = options.getMaxLineWidth() - printer.indentation();
        
        switch (options.getStyle().styleScalar(context, maxLength, value, 0, value.length)) {
        case BLOCK_LITERAL:
            
            printer.beginBlockScalar(BlockScalarType.LITERAL, ChompingStyle.CLIP);            
            printText(options.getStyle().formatLiteral(maxLength, value, 0, value.length), value);
            printer.endBlockScalar();            
            break;
            
        case BLOCK_FOLDED:
            printer.beginBlockScalar(BlockScalarType.FOLDED, ChompingStyle.CLIP);
            printText(options.getStyle().formatFolded(maxLength, value, 0, value.length), value);
            printer.endBlockScalar();                        
            break;
            
        case FLOW_PLAIN:
            printFlowPlainScalar(value);
            break;
            
        case FLOW_SINGLE_QUOTED:
            printer.beginFlowScalar(FlowScalarType.SINGLE_QUOTED);
            printer.print(value, 0, value.length);
            printer.endFlowScalar();            
            break;
            
        case FLOW_DOUBLE_QUOTED:
            printer.beginFlowScalar(FlowScalarType.DOUBLE_QUOTED);
            printer.print(value, 0, value.length);
            printer.endFlowScalar();
            break;
        }        
    }
    
    private final void printFlowPlainScalar(char[] value) throws YamlPrinterException {
        printer.beginFlowScalar(FlowScalarType.PLAIN);
        printer.print(value, 0, value.length);
        printer.endFlowScalar();
    }

    private static final YamlPrintStyle.Context toStyleContext(YamlPrinter.Context context) {
        switch (context) {
        case BLOCK_MAPPING_KEY:
            return YamlPrintStyle.Context.BLOCK_MAPPING_KEY;
            
        case BLOCK_MAPPING_VALUE:
            return YamlPrintStyle.Context.BLOCK_MAPPING_VALUE;
            
        case COMPACT_BLOCK_SEQUENCE:
        case BLOCK_SEQUENCE:
            return YamlPrintStyle.Context.SEQUENCE;
            
        case DOCUMENT_BEGIN:
        case DOCUMENT_END:
            return YamlPrintStyle.Context.DOCUMENT;
            
        default:
            throw new IllegalStateException();
        }
    }
    
    private final void printText(Collection<TextPrintIndex> indices, char[] value) throws YamlPrinterException {
        
        for (TextPrintIndex index : indices) {
            
            if (index.getOffset() != -1 && index.getLength() != -1) {
                printer.print(value, index.getOffset(), index.getLength());                    
            }
            
            if (TextPrintIndex.Type.PRINT_LN.equals(index.getType())) {
                printer.println();
            }
        }        
    }
}
