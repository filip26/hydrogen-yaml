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
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import com.apicatalog.yaml.impl.YamlMappingBuilderImpl;
import com.apicatalog.yaml.impl.YamlScalarImpl;
import com.apicatalog.yaml.impl.YamlSequenceBuilderImpl;
import com.apicatalog.yaml.impl.YamlWriterBuilderImpl;
import com.apicatalog.yaml.parser.YamlParser;
import com.apicatalog.yaml.writer.YamlPrintStyle;
import com.apicatalog.yaml.writer.YamlWriter;

public final class Yaml {

    private Yaml() {}
    
    public static final YamlParser createParser(InputStream input) {
        throw new UnsupportedOperationException();
    }

    public static final YamlParser createParser(Reader reader) {
        if (reader == null) {
            throw new IllegalArgumentException();
        }

        throw new UnsupportedOperationException();
    }
    
    public static final YamlWriterBuilder createWriter(OutputStream output) {
        if (output == null) {
            throw new IllegalArgumentException();
        }

        return new YamlWriterBuilderImpl(new OutputStreamWriter(output));
    }
    
    public static final YamlWriterBuilder createWriter(Writer writer) {
        if (writer == null) {
            throw new IllegalArgumentException();
        }
        
        return new YamlWriterBuilderImpl(writer);
    }
    
    public static YamlWriter createWriter(Writer writer, YamlPrintStyle style) {
        if (writer == null) {
            throw new IllegalArgumentException();
        }
        if (style == null) {
            throw new IllegalArgumentException();
        }

        return YamlWriterBuilderImpl.build(writer, style);
    }

    public static YamlWriter createWriter(OutputStream output, YamlPrintStyle style) {
        if (output == null) {
            throw new IllegalArgumentException();
        }
        if (style == null) {
            throw new IllegalArgumentException();
        }

        return YamlWriterBuilderImpl.build(new OutputStreamWriter(output), style);
    }

    public static final YamlMappingBuilder createMappingBuilder() {
        return new YamlMappingBuilderImpl();
    }

    public static final YamlMappingBuilder createMappingBuilder(YamlMapping mapping) {
        if (mapping == null) {
            throw new IllegalArgumentException();
        }

        return YamlMappingBuilderImpl.of(mapping);
    }

    public static final YamlSequenceBuilder createSequenceBuilder() {
        return new YamlSequenceBuilderImpl();
    }
    
    public static final YamlSequenceBuilder createSequenceBuilder(YamlSequence sequence) {
        if (sequence == null) {
            throw new IllegalArgumentException();
        }

        return YamlSequenceBuilderImpl.of(sequence);
    }
    
    public static final YamlScalar createScalar(String value) {        
        if (value == null) {
            throw new IllegalArgumentException();
        }
        
        return new YamlScalarImpl(value);        
    }
}
