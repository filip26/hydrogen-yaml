package com.apicatalog.yaml.writer;

import java.util.Map;
import java.util.function.Predicate;

import com.apicatalog.yaml.YamlException;
import com.apicatalog.yaml.YamlNode;
import com.apicatalog.yaml.YamlScalar;
import com.apicatalog.yaml.printer.FlowScalarType;
import com.apicatalog.yaml.printer.YamlPrinter;
import com.apicatalog.yaml.printer.YamlPrinterException;
import com.apicatalog.yaml.writer.style.YamlStyle;

public class YamlWriterImpl implements YamlWriter {

    private final YamlPrinter printer;
    private final YamlStyle style;
    
    public YamlWriterImpl(YamlPrinter printer, YamlStyle style) {
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

    private final void writeScalar(final YamlStyle.Context context, final YamlScalar scalar) throws YamlPrinterException {
        
        final char[] value = scalar.getValue().toCharArray();
        
        switch (style.scalar(context, value, 0, value.length)) {
        case BLOCK_LITERAL:
            break;
            
        case BLOCK_FOLDED:
            break;
            
        case FLOW_PLAIN:
            printer.beginFlowScalar(FlowScalarType.PLAIN);
            printer.print(value, 0, value.length);
            printer.endFlowScalar();
            break;
            
        case FLOW_SINGLE_QUOTED:
            break;
            
        case FLOW_DOUBLE_QUOTED:
            printer.beginFlowScalar(FlowScalarType.DOUBLE_QUOTED);
            printer.print(value, 0, value.length);
            printer.endFlowScalar();
            break;
        }        
    }

    private static final YamlStyle.Context toStyleContext(YamlPrinter.Context context) {
        switch (context) {
        case BLOCK_MAPPING_KEY:
            return YamlStyle.Context.BLOCK_MAPPING_KEY;
            
        case BLOCK_MAPPING_VALUE:
            return YamlStyle.Context.BLOCK_MAPPING_VALUE;
            
        case COMPACT_BLOCK_SEQUENCE:
        case BLOCK_SEQUENCE:
            return YamlStyle.Context.SEQUENCE;
            
        case DOCUMENT_BEGIN:
        case DOCUMENT_END:
            return YamlStyle.Context.DOCUMENT;
            
        default:
            throw new IllegalStateException();
        }
    }
    
    private static final Predicate<Character> IS_PRINTABLE = ch ->
                                                        ch == 0x9 || ch == 0xA || ch == 0xD
                                                        || (ch >= 0x20 && ch <= 0x7E)
                                                        || ch == 0x85
                                                        || (ch >= 0xA0 && ch <= 0xD7FF)
                                                        || (ch >= 0xE000 && ch <= 0xFFFD)
                                                        ;
}
