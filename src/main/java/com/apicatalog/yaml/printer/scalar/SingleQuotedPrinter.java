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
package com.apicatalog.yaml.printer.scalar;

import java.io.IOException;

import com.apicatalog.yaml.printer.IndentedPrinter;

public final class SingleQuotedPrinter {
    
    private final IndentedPrinter printer;
    
    public SingleQuotedPrinter(final IndentedPrinter printer) {
        this.printer = printer;
    }
    
    public void print(int maxLineLength, char[] chars, int offset, int length) throws IOException {
        
        int lineIndex = 0;
        int lastSpaceIndex = 0;
      
        for (int i = 0; i < length; i++) {
          
            if ('\n' == chars[i + offset]) {
                
                if (i - lineIndex > 0) {
                    singleEscape(chars, offset + lineIndex, i - lineIndex);
                }

                printer.println();
                printer.println();
                
                lineIndex = i + 1;
                lastSpaceIndex = i + 1;
                
            } else if (' ' == chars[i + offset]) {          
                lastSpaceIndex = i + 1;                                    

            } else if (i - lineIndex >=  maxLineLength) {   
                
                singleEscape(chars, offset + lineIndex, lastSpaceIndex - lineIndex - 1);
                printer.println();
                lineIndex = lastSpaceIndex;                      
            }
        }
  
        if (lineIndex < length) {
            singleEscape(chars, offset + lineIndex, length - lineIndex);
        }
    }
    
    private void singleEscape(final char[] chars, final int offset, final int length) throws IOException {
        
        int start = 0;
        
        // find next single quote
        for (int i=0; i < length; i++) {
            if (chars[offset + i] == '\'') {
                
                // flush previous chars
                if (i >= start) {
                    printer.print(chars, offset + start, i - start + 1);
                }
                printer.print('\'');
                start = i + 1;
            }
        }
        // flush previous chars
        if (length > start) {
            printer.print(chars, offset + start, length - start);
        }
    }
}
