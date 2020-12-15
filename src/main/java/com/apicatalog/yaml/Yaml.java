/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.apicatalog.yaml;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import com.apicatalog.yaml.node.YamlMapping;
import com.apicatalog.yaml.node.YamlScalar;
import com.apicatalog.yaml.node.YamlSequence;
import com.apicatalog.yaml.parser.YamlParser;
import com.apicatalog.yaml.provider.DefaultYamlProvider;
import com.apicatalog.yaml.provider.YamlProvider;
import com.apicatalog.yaml.writer.YamlPrintStyle;
import com.apicatalog.yaml.writer.YamlWriter;

/**
 * This class provides common methods for YAML processing.
 * 
 * All methods are thread-safe.
 */
public final class Yaml {

    private static final YamlProvider DEFAULT_PROVIDER = new DefaultYamlProvider();
    
    private Yaml() {}
    
    /**
     * Creates a YAML parser from {@link InputStream}.
     *
     * @param in byte stream from which YAML is to be read
     * @return a YAML parser
     */
    public static final YamlParser createParser(final InputStream in) {
        return DEFAULT_PROVIDER.createParser(in);
    }

    /**
     * Creates a YAML parser from {@link Reader}.
     *
     * @param reader a reader from which YAML is to be read
     * @return a YAML parser
     */
    public static final YamlParser createParser(final Reader reader) {
        return DEFAULT_PROVIDER.createParser(reader);
    }
    
    /**
     * Creates a YAML writer builder.
     *
     * @param out byte stream to which YAML is written
     * @return a YAML writer builder
     */
    public static final YamlWriterBuilder createWriterBuilder(final OutputStream out) {
        return DEFAULT_PROVIDER.createWriterBuilder(out);
    }

    /**
     * Creates a YAML writer builder.
     *
     * @param writer a writer to which YAML is written
     * @return a YAML writer builder
     */
    public static final YamlWriterBuilder createWriterBuilder(final Writer writer) {
        return DEFAULT_PROVIDER.createWriterBuilder(writer);
    }

    /**
     * Creates a YAML writer.
     *
     * @param writer a writer to which YAML is written
     * @param style a print style
     * @return a YAML writer
     */
    public static YamlWriter createWriter(final Writer writer, final YamlPrintStyle style) {
        return DEFAULT_PROVIDER.createWriter(writer, style);
    }

    /**
     * Creates a YAML writer.
     *
     * @param out byte stream to which YAML is written
     * @param style a print style
     * @return a YAML writer
     */
    public static YamlWriter createWriter(final OutputStream out, final YamlPrintStyle style) {
        return DEFAULT_PROVIDER.createWriter(out, style);
    }

    /**
     * Creates a YAML mapping builder
     *
     * @return a YAML mapping builder
     */
    public static final YamlMappingBuilder createMappingBuilder() {
        return DEFAULT_PROVIDER.createMappingBuilder();
    }

    /**
     * Creates a YAML mapping builder. Initialized with the given mapping.
     *
     * @param mapping an initial mapping
     * @return a YAML mapping builder
     */
    public static final YamlMappingBuilder createMappingBuilder(final YamlMapping mapping) {
        return DEFAULT_PROVIDER.createMappingBuilder(mapping);
    }

    /**
     * Creates a YAML sequence builder
     *
     * @return a YAML sequence builder
     */
    public static final YamlSequenceBuilder createSequenceBuilder() {
        return DEFAULT_PROVIDER.createSequenceBuilder();
    }
    
    /**
     * Creates a YAML sequence builder. Initialized with the given sequence.
     *
     * @param sequence an initial sequence
     * @return a YAML sequence builder
     */
    public static final YamlSequenceBuilder createSequenceBuilder(final YamlSequence sequence) {
        return DEFAULT_PROVIDER.createSequenceBuilder(sequence);
    }
    
    /**
     * Creates a YAML scalar.
     *
     * @param value the value represented as a string
     * @return a YAML scalar
     */
    public static final YamlScalar createScalar(final String value) {
        return DEFAULT_PROVIDER.createScalar(value);        
    }
}
