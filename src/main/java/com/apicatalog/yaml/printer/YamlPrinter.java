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
package com.apicatalog.yaml.printer;

import java.io.Closeable;
import java.io.IOException;

public interface YamlPrinter extends Closeable {

    YamlPrinter printScalar(char[] chars, int offset, int length) throws IOException;
    
    YamlPrinter printFoldedScalar(char[] chars, int offset, int length) throws IOException;
    
    YamlPrinter printLiteralScalar(char[] chars, int offset, int length) throws IOException;
    
    YamlPrinter printPlainScalar(char[] chars, int offset, int length) throws IOException;
    
    YamlPrinter printSingleQuotedScalar(char[] chars, int offset, int length) throws IOException;
    
    YamlPrinter printDoubleQuotedScalar(char[] chars, int offset, int length) throws IOException;
    
    YamlPrinter beginBlockSequence() throws IOException;

    YamlPrinter endBlockSequence() throws IOException;

    YamlPrinter beginBlockMapping() throws IOException;
    
    YamlPrinter endBlockdMapping() throws IOException;
    
    YamlPrinter printNull() throws IOException;

    @Override
    void close() throws IOException;
}
