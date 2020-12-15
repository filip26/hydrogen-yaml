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
package com.apicatalog.yaml.writer;

import java.io.IOException;
import java.util.Map;

import com.apicatalog.yaml.YamlException;
import com.apicatalog.yaml.node.YamlNode;
import com.apicatalog.yaml.printer.YamlPrinter;

public class DefaultYamlWriter implements YamlWriter {

    private final YamlPrinter printer;
    
    public DefaultYamlWriter(YamlPrinter printer) {
        this.printer  = printer;
    }
    
    @Override
    public void write(final YamlNode node) throws YamlException, IOException {

        switch (node.getNodeType()) {
        case MAPPING:
            printer.beginBlockMapping();
            
            for (final Map.Entry<String, YamlNode> entry : node.asMapping().entrySet()) {
                writeScalar(entry.getKey());
                write(entry.getValue());
            }
            
            printer.endBlockdMapping();
            break;
            
        case NULL:
            printer.printNull();
            break;
            
        case SCALAR:
            writeScalar(node.asScalar().getValue());
            break;
            
        case SEQUENCE:
            
            printer.beginBlockSequence();
            
            for (final YamlNode item : node.asSequence()) {
                write(item);
            }

            printer.endBlockSequence();
            break;
            
        }


    }
    
    @Override
    public void close() throws IOException {
        printer.close();
    }
    
    private final void writeScalar(String scalar) throws IOException {

        final char[] value = scalar.toCharArray();
        
        printer.printScalar(value, 0, value.length);
    }
}
