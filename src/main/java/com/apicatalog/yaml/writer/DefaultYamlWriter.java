package com.apicatalog.yaml.writer;

import java.io.IOException;
import java.util.Map;

import com.apicatalog.yaml.YamlException;
import com.apicatalog.yaml.YamlNode;
import com.apicatalog.yaml.printer.YamlPrinter;

public class DefaultYamlWriter implements YamlWriter {

    private final YamlPrinter printer;
    
    public DefaultYamlWriter(YamlPrinter printer) {
        this.printer  = printer;
    }
    
    @Override
    public void write(final YamlNode node) throws YamlException, IOException {

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


    }
    
    @Override
    public void close() throws IOException {
        printer.close();
    }
    
    private final void writeScalar(String scalar) throws IOException {

        final char[] value = scalar.toCharArray();
        
        printer.printScalar(value, 0, value.length);
    }
}
