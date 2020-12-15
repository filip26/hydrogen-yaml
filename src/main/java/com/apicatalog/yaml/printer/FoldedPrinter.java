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

import java.io.IOException;

final class FoldedPrinter {
    
    enum Context { LINE, INDENTED }

    private final IndentedPrinter printer;
    
    public FoldedPrinter(final IndentedPrinter printer) {
        this.printer = printer;
    }
    
    public void print(int maxLineLength, char[] chars, int offset, int length) throws IOException {
        
        Context context = Context.LINE;

        int lineIndex = 0;
        int lastSpaceIndex = 0;

        for (int i = 0; i < length; i++) {
            
            final char ch = chars[i + offset];
            
            if (Context.LINE.equals(context)) {

                if ('\n' == ch) {
                    
                    if (i - lineIndex > 0) {
                        printer.print(chars, offset + lineIndex, i - lineIndex);
                        printer.println();
                    }
                    
                    if (i + 1 < length && chars[i + offset + 1] == ' ') {
                        context = Context.INDENTED;
                        
                    } else {
                        printer.println();
                    }

                    lineIndex = i + 1;
                    lastSpaceIndex = i;
                    
                } else if (' ' == ch) {
                    lastSpaceIndex = i;                                    

                } else if (i - lineIndex >=  maxLineLength && lastSpaceIndex > lineIndex) { 
                    printer.print(chars, offset + lineIndex, lastSpaceIndex - lineIndex);
                    printer.println();
                    lineIndex = lastSpaceIndex + 1;
                }
          

            } else if (Context.INDENTED.equals(context) && '\n' == ch) {
                
                if (i - lineIndex > 0) {
                    printer.print(chars, offset + lineIndex, i - lineIndex);                   
                }
                
                printer.println();
                
                if (i + 1 >= length || chars[i + offset + 1] != ' ' && chars[i + offset + 1] != '\n') {           
                    context = Context.LINE;
                }

                lineIndex = i + 1;
                lastSpaceIndex = i;                
            }
        }
        
        if (lineIndex < length) {
            printer.print(chars, offset + lineIndex, length - lineIndex);
        }
    }
}
