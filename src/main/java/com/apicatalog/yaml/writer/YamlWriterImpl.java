package com.apicatalog.yaml.writer;

import java.util.Map;

import com.apicatalog.yaml.YamlException;
import com.apicatalog.yaml.YamlNode;
import com.apicatalog.yaml.printer.FlowScalarType;
import com.apicatalog.yaml.printer.YamlPrinter;
import com.apicatalog.yaml.printer.YamlPrinterException;

public class YamlWriterImpl implements YamlWriter {

    private final YamlPrinter printer;
    
    public YamlWriterImpl(YamlPrinter printer) {
        this.printer  = printer;
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
                break;
                
            case SEQUENCE:
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

}
