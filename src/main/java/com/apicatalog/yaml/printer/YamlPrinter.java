package com.apicatalog.yaml.printer;

import java.io.Closeable;
import java.io.IOException;

public interface YamlPrinter extends Closeable {

    YamlPrinter printScalar(char[] chars, int offset, int length) throws IOException;
    
    YamlPrinter printFoldedScalar(char[] chars, int offset, int length) throws IOException;
    
    YamlPrinter printLiteralScalar(char[] chars, int offset, int length) throws IOException;
    
    YamlPrinter printPlainScalar(char[] chars, int offset, int length) throws IOException;
    
    YamlPrinter printSingleQuotedScalar(char[] chars, int offset, int length) throws IOException;
    
    YamlPrinter printDoubleQuotedScalar(char[] chars, int offset, int length) throws IOException;
    
    YamlPrinter beginBlockSequence() throws IOException;

    YamlPrinter endBlockSequence() throws IOException;

    YamlPrinter beginBlockMapping() throws IOException;
    
    YamlPrinter endBlockdMapping() throws IOException;
    
    YamlPrinter printNull() throws IOException;

    @Override
    void close() throws IOException;
}
