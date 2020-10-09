package com.apicatalog.yaml.impl;

import java.io.Writer;

import com.apicatalog.yaml.YamlWriterBuilder;
import com.apicatalog.yaml.printer.DefaultYamlPrinter;
import com.apicatalog.yaml.printer.IndentedPrinter;
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
    public YamlWriterBuilder maxLineWidth(int maxLineWidth) {
        style.setMaxLineWidth(maxLineWidth);
        return this;
    }

    @Override
    public YamlWriter build() {
        return build(writer, style);
    }
    
    public static final YamlWriter build(final Writer writer, final YamlPrintStyle style) {
        return new DefaultYamlWriter(new DefaultYamlPrinter(new IndentedPrinter(writer), style));
    }
}
