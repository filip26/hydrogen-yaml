package com.apicatalog.yaml.provider;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import com.apicatalog.yaml.YamlMappingBuilder;
import com.apicatalog.yaml.YamlSequenceBuilder;
import com.apicatalog.yaml.YamlWriterBuilder;
import com.apicatalog.yaml.node.YamlMapping;
import com.apicatalog.yaml.node.YamlScalar;
import com.apicatalog.yaml.node.YamlSequence;
import com.apicatalog.yaml.parser.YamlParser;
import com.apicatalog.yaml.writer.YamlPrintStyle;
import com.apicatalog.yaml.writer.YamlWriter;

public interface YamlProvider {
    
    static YamlProvider provider() {
        return null;
    }

    default YamlParser createParser(final InputStream inputStream) {
        throw new UnsupportedOperationException();
    }

    default YamlParser createParser(final Reader reader) {
        throw new UnsupportedOperationException();
    }
    
    default YamlWriterBuilder createWriterBuilder(final OutputStream outputStream) {
        throw new UnsupportedOperationException();
    }
    
    default  YamlWriterBuilder createWriterBuilder(final Writer writer) {
        throw new UnsupportedOperationException();
    }
    
    default  YamlWriter createWriter(final Writer writer, final YamlPrintStyle style) {
        throw new UnsupportedOperationException();
    }

    default  YamlWriter createWriter(final OutputStream output, final YamlPrintStyle style) {
        throw new UnsupportedOperationException();
    }

    YamlMappingBuilder createMappingBuilder();

    YamlMappingBuilder createMappingBuilder(YamlMapping mapping);

    YamlSequenceBuilder createSequenceBuilder();
    
    YamlSequenceBuilder createSequenceBuilder(YamlSequence sequence);
    
    YamlScalar createScalar(String value);
}
