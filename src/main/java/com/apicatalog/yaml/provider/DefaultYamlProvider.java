package com.apicatalog.yaml.provider;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.apicatalog.yaml.node.YamlMapping;
import com.apicatalog.yaml.node.YamlScalar;
import com.apicatalog.yaml.node.YamlSequence;
import com.apicatalog.yaml.node.builder.YamlMappingBuilder;
import com.apicatalog.yaml.node.builder.YamlSequenceBuilder;
import com.apicatalog.yaml.writer.YamlPrintStyle;
import com.apicatalog.yaml.writer.YamlWriter;
import com.apicatalog.yaml.writer.YamlWriterBuilder;

public final class DefaultYamlProvider implements YamlProvider {

    @Override
    public final YamlWriterBuilder createWriterBuilder(final OutputStream outputStream) {
        if (outputStream == null) {
            throw new IllegalArgumentException();
        }

        return new YamlWriterBuilderImpl(new OutputStreamWriter(outputStream));
    }
    
    @Override
    public final YamlWriterBuilder createWriterBuilder(final Writer writer) {
        if (writer == null) {
            throw new IllegalArgumentException();
        }
        
        return new YamlWriterBuilderImpl(writer);
    }
    
    @Override
    public YamlWriter createWriter(final Writer writer, final YamlPrintStyle style) {
        if (writer == null) {
            throw new IllegalArgumentException();
        }
        if (style == null) {
            throw new IllegalArgumentException();
        }

        return YamlWriterBuilderImpl.build(writer, style);
    }

    @Override
    public YamlWriter createWriter(final OutputStream output, final YamlPrintStyle style) {
        if (output == null) {
            throw new IllegalArgumentException();
        }
        if (style == null) {
            throw new IllegalArgumentException();
        }

        return YamlWriterBuilderImpl.build(new OutputStreamWriter(output), style);
    }

    @Override
    public final YamlMappingBuilder createMappingBuilder() {
        return new YamlMappingBuilderImpl();
    }

    @Override
    public final YamlMappingBuilder createMappingBuilder(final YamlMapping mapping) {
        if (mapping == null) {
            throw new IllegalArgumentException();
        }

        return YamlMappingBuilderImpl.of(mapping);
    }

    @Override
    public final YamlSequenceBuilder createSequenceBuilder() {
        return new YamlSequenceBuilderImpl();
    }
    
    @Override
    public final YamlSequenceBuilder createSequenceBuilder(final YamlSequence sequence) {
        if (sequence == null) {
            throw new IllegalArgumentException();
        }

        return YamlSequenceBuilderImpl.of(sequence);
    }
    
    @Override
    public final YamlScalar createScalar(final String value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }
        
        return new YamlScalarImpl(value);        
    }
}