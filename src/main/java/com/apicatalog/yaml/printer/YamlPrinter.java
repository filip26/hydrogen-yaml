package com.apicatalog.yaml.printer;

public interface YamlPrinter {

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
    
    YamlPrinter beginBlockSequence(boolean compacted) throws YamlPrinterException;

    YamlPrinter endBlockSequence() throws YamlPrinterException;

    YamlPrinter beginBlockMapping() throws YamlPrinterException;
    
    YamlPrinter endBlockdMapping() throws YamlPrinterException;
    
    YamlPrinter skip() throws YamlPrinterException;
}
