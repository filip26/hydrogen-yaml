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

public final class Yaml {

    private static final YamlProvider DEFAULT_PROVIDER = new DefaultYamlProvider();
    
    private Yaml() {}
    
    public static final YamlParser createParser(final InputStream inputStream) {
        return DEFAULT_PROVIDER.createParser(inputStream);
    }

    public static final YamlParser createParser(final Reader reader) {
        return DEFAULT_PROVIDER.createParser(reader);
    }
    
    public static final YamlWriterBuilder createWriter(final OutputStream outputStream) {
        return DEFAULT_PROVIDER.createWriter(outputStream);
    }
    
    public static final YamlWriterBuilder createWriter(final Writer writer) {
        return DEFAULT_PROVIDER.createWriter(writer);
    }
    
    public static YamlWriter createWriter(final Writer writer, final YamlPrintStyle style) {
        return DEFAULT_PROVIDER.createWriter(writer, style);
    }

    public static YamlWriter createWriter(final OutputStream output, final YamlPrintStyle style) {
        return DEFAULT_PROVIDER.createWriter(output, style);
    }

    public static final YamlMappingBuilder createMappingBuilder() {
        return DEFAULT_PROVIDER.createMappingBuilder();
    }

    public static final YamlMappingBuilder createMappingBuilder(final YamlMapping mapping) {
        return DEFAULT_PROVIDER.createMappingBuilder(mapping);
    }

    public static final YamlSequenceBuilder createSequenceBuilder() {
        return DEFAULT_PROVIDER.createSequenceBuilder();
    }
    
    public static final YamlSequenceBuilder createSequenceBuilder(final YamlSequence sequence) {
        return DEFAULT_PROVIDER.createSequenceBuilder(sequence);
    }
    
    public static final YamlScalar createScalar(final String value) {
        return DEFAULT_PROVIDER.createScalar(value);        
    }
}
