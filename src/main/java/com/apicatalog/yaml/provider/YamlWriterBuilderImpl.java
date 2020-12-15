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
package com.apicatalog.yaml.provider;

import java.io.Writer;

import com.apicatalog.yaml.printer.DefaultYamlPrinter;
import com.apicatalog.yaml.writer.DefaultYamlWriter;
import com.apicatalog.yaml.writer.YamlPrintStyle;
import com.apicatalog.yaml.writer.YamlWriter;
import com.apicatalog.yaml.writer.YamlWriterBuilder;

final class YamlWriterBuilderImpl implements YamlWriterBuilder {

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
        return new DefaultYamlWriter(new DefaultYamlPrinter(writer, style));
    }

    @Override
    public YamlWriterBuilder compactArrays(boolean enable) {
        style.setCompactArrays(enable);
        return this;
    }
}
