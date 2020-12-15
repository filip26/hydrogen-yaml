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
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.Deque;

final class IndentedPrinter implements Closeable {

    private final Writer writer;
    
    private boolean newLine;
    private int printed;
    
    private static final char[] SPACES = { 
                                    ' ', ' ', ' ', ' ', ' ', 
                                    ' ', ' ', ' ', ' ', ' ', 
                                    ' ', ' ', ' ', ' ', ' ',
                                    ' ', ' ', ' ', ' ', ' ',
                                    ' ', ' ', ' ', ' ', ' ',
                                    ' ', ' ', ' ', ' ', ' ',
                                    };
        
    private Deque<Integer> indentation;
    
    public IndentedPrinter(Writer writer) {
        this.writer = writer;
        this.indentation = new ArrayDeque<>(30);
        this.indentation.push(0);
        this.newLine = false;
        this.printed = 0;
    }
    
    public IndentedPrinter beginBlock() {
        indentation.push(indentation.peek() + 2);
        return this;
    }

    public IndentedPrinter endBlock() {
        indentation.pop();
        newLine = true;
        return this;
    }
    
    public IndentedPrinter beginFlow() {
        indentation.push(newLine ? indentation.peek() : printed);
        return this;
    }
    
    public IndentedPrinter endFlow() {
        indentation.pop();
        return this;        
    }

    public IndentedPrinter println() throws IOException {

        if (newLine) {

            writer.write(new char[] {'\n'});
            printed = 0;
            return this;                
        }
        
        this.newLine = true;
        return this;
    }

    public IndentedPrinter print(char[] chars)  throws IOException {
        return print(chars, 0, chars.length);
    }
    
    public IndentedPrinter print(char[] chars, int offset, int length)  throws IOException {
        
        if (newLine) {
            printIndentation();
        }
            
        printed += length;
        writer.write(chars, offset, length);
            
        return this;
    }

    public IndentedPrinter print(char ch)  throws IOException {
        return print(new char[] {ch}, 0, 1);
    }

    private final void printIndentation() throws IOException {
        
        writer.write(new char[] {'\n'});
        printed = indentation.peek();
        newLine = false;

        int spaces = indentation.peek();
        
        if (spaces > 0) {
            while (spaces >= SPACES.length) {
                writer.write(SPACES, 0, SPACES.length);    
                spaces -= SPACES.length;
            }
            if (spaces > 0) {
                writer.write(SPACES, 0, spaces);
            }
        }
    }

    public int indentation() {
        return indentation.peek();
    }
    
    @Override
    public void close() throws IOException {
        writer.close();
    }

    public boolean isNewLine() {
        return newLine;
    }
}
