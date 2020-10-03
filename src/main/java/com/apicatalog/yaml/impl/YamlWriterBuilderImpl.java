package com.apicatalog.yaml.impl;

import java.io.OutputStream;
import java.io.Writer;

import com.apicatalog.yaml.YamlWriterBuilder;
import com.apicatalog.yaml.printer.DefaultYamlPrinter;
import com.apicatalog.yaml.printer.IndentedkPrinter;
import com.apicatalog.yaml.writer.DefaultYamlWriter;
import com.apicatalog.yaml.writer.YamlPrintStyle;
import com.apicatalog.yaml.writer.YamlWriter;

public class YamlWriterBuilderImpl implements YamlWriterBuilder {

    private final YamlPrintStyle style;
    
    private final Writer writer;
    
    public YamlWriterBuilderImpl(Writer writer) {
        this.writer = writer;
        this.style = new YamlPrintStyle();
    }
    
    @Override
    public YamlWriterBuilder maxiumWidth(int maximumWidth) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public YamlWriter build() {
        return new DefaultYamlWriter(new DefaultYamlPrinter(new IndentedkPrinter(writer)), style);
    }
}
