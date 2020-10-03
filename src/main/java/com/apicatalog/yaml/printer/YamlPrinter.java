package com.apicatalog.yaml.printer;

import java.util.Collection;

public interface YamlPrinter {

    enum Context { 
        DOCUMENT_BEGIN,
        DOCUMENT_END,
        BLOCK_SCALAR,
        FLOW_PLAIN_SCALAR,
        FLOW_DOUBLE_QUOTED_SCALAR,
        FLOW_SINGLE_QUOTED_SCALAR,
        BLOCK_SEQUENCE,
        @Deprecated
        COMPACT_BLOCK_SEQUENCE, 
        BLOCK_MAPPING_KEY,
        BLOCK_MAPPING_VALUE,
        }

    Collection<Context> getContext();
    
    YamlPrinter beginBlockScalar(BlockScalarType type, ChompingStyle chomping) throws YamlPrinterException;
    
    YamlPrinter endBlockScalar() throws YamlPrinterException;
    
    YamlPrinter beginFlowScalar(FlowScalarType type) throws YamlPrinterException;
    
    YamlPrinter endFlowScalar() throws YamlPrinterException;

    default YamlPrinter print(char[] chars) throws YamlPrinterException {
        return print(chars, 0, chars.length);
    }
    
    YamlPrinter print(char[] chars, int offset, int length) throws YamlPrinterException;
    
    YamlPrinter println() throws YamlPrinterException;

    default YamlPrinter beginBlockSequence() throws YamlPrinterException {
        return beginBlockSequence(false);
    }
    
    @Deprecated
    YamlPrinter beginBlockSequence(boolean compacted) throws YamlPrinterException;

    YamlPrinter endBlockSequence() throws YamlPrinterException;

    YamlPrinter beginBlockMapping() throws YamlPrinterException;
    
    YamlPrinter endBlockdMapping() throws YamlPrinterException;
    
    YamlPrinter printNull() throws YamlPrinterException;

    int indentation();
}
