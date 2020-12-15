package com.apicatalog.yaml.provider;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import com.apicatalog.yaml.node.YamlMapping;
import com.apicatalog.yaml.node.YamlScalar;
import com.apicatalog.yaml.node.YamlSequence;
import com.apicatalog.yaml.node.builder.YamlMappingBuilder;
import com.apicatalog.yaml.node.builder.YamlSequenceBuilder;
import com.apicatalog.yaml.parser.YamlParser;
import com.apicatalog.yaml.writer.YamlPrintStyle;
import com.apicatalog.yaml.writer.YamlWriter;
import com.apicatalog.yaml.writer.YamlWriterBuilder;

/**
 * Thread-safe provider of YAML processing methods. 
 */
public interface YamlProvider {
    
    static YamlProvider provider() {
        return null;
    }

    /**
     * Creates a YAML parser from {@link InputStream}.
     *
     * @param in byte stream from which YAML is to be read
     * @return a YAML parser
     */
    default YamlParser createParser(final InputStream inputStream) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a YAML parser from {@link Reader}.
     *
     * @param reader a reader from which YAML is to be read
     * @return a YAML parser
     */
    default YamlParser createParser(final Reader reader) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Creates a YAML writer builder.
     *
     * @param out byte stream to which YAML is written
     * @return a YAML writer builder
     */
    default YamlWriterBuilder createWriterBuilder(final OutputStream outputStream) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Creates a YAML writer builder.
     *
     * @param writer a writer to which YAML is written
     * @return a YAML writer builder
     */
    default  YamlWriterBuilder createWriterBuilder(final Writer writer) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Creates a YAML writer.
     *
     * @param writer a writer to which YAML is written
     * @param style a print style
     * @return a YAML writer
     */
    default  YamlWriter createWriter(final Writer writer, final YamlPrintStyle style) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a YAML writer.
     *
     * @param out byte stream to which YAML is written
     * @param style a print style
     * @return a YAML writer
     */
    default  YamlWriter createWriter(final OutputStream output, final YamlPrintStyle style) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a YAML mapping builder
     *
     * @return a YAML mapping builder
     */
    YamlMappingBuilder createMappingBuilder();

    /**
     * Creates a YAML mapping builder. Initialized with the given mapping.
     *
     * @param mapping an initial mapping
     * @return a YAML mapping builder
     */
    YamlMappingBuilder createMappingBuilder(YamlMapping mapping);

    /**
     * Creates a YAML sequence builder
     *
     * @return a YAML sequence builder
     */
    YamlSequenceBuilder createSequenceBuilder();
    
    /**
     * Creates a YAML sequence builder. Initialized with the given sequence.
     *
     * @param sequence an initial sequence
     * @return a YAML sequence builder
     */
    YamlSequenceBuilder createSequenceBuilder(YamlSequence sequence);
    
    /**
     * Creates a YAML scalar.
     *
     * @param value the value represented as a string
     * @return a YAML scalar
     */
    YamlScalar createScalar(String value);
}
