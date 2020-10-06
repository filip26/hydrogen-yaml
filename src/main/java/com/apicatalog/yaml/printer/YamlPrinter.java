package com.apicatalog.yaml.printer;

import java.io.Closeable;
import java.io.IOException;

public interface YamlPrinter extends Closeable {

    YamlPrinter printScalar(char[] chars, int offset, int length) throws YamlPrinterException, IOException;
    
    YamlPrinter printFoldedScalar(char[] chars, int offset, int length) throws YamlPrinterException, IOException;
    
    YamlPrinter printLiteralScalar(char[] chars, int offset, int length) throws YamlPrinterException, IOException;
    
    YamlPrinter printPlainScalar(char[] chars, int offset, int length) throws YamlPrinterException, IOException;
    
    YamlPrinter printSingleQuotedScalar(char[] chars, int offset, int length) throws YamlPrinterException, IOException;
    
    YamlPrinter printDoubleQuotedScalar(char[] chars, int offset, int length) throws YamlPrinterException, IOException;
    
    YamlPrinter beginBlockSequence() throws YamlPrinterException, IOException;

    YamlPrinter endBlockSequence() throws YamlPrinterException, IOException;

    YamlPrinter beginBlockMapping() throws YamlPrinterException, IOException;
    
    YamlPrinter endBlockdMapping() throws YamlPrinterException, IOException;
    
    YamlPrinter printNull() throws YamlPrinterException, IOException;

    @Override
    void close() throws IOException;
}
