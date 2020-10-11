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

public class DoubleQuotedPrinter {
    
    final IndentedPrinter printer;
    
    public DoubleQuotedPrinter(final IndentedPrinter printer) {
        this.printer = printer;
    }
    
    public void printFolded(int maxLineLength, char[] chars, int offset, int length) throws IOException {
        
        int lineIndex = 0;
        int lastSpaceIndex = 0;
      
        boolean prevSpace = true;
        
        for (int i = 0; i < length; i++) {

            if ('\n' == chars[i + offset]) {
                
                if (i - lineIndex > 0) {
                    lineIndex += printLeadingSpace(chars, offset + lineIndex);
                    
                    boolean trailingSpace = hasTrailingSpace(chars, offset + lineIndex, i - lineIndex);
                    
                    doubleEscape(chars, offset + lineIndex, i - lineIndex - (trailingSpace ? 1 : 0));
                    
                    if (trailingSpace) {
                        printer.print(new char[] { '\\', ' '});
                    }
                }

                if (!printer.isNewLine()) {
                    printer.println();
                }
                printer.println();
                
                lineIndex = i + 1;
                lastSpaceIndex = i;
                prevSpace = false;
                
            } else if ((i - lineIndex) >=  maxLineLength && lastSpaceIndex > lineIndex) {   
                lineIndex += printLeadingSpace(chars, offset + lineIndex);
                
                boolean trailingSpace = hasTrailingSpace(chars, offset + lineIndex, lastSpaceIndex - lineIndex - 1);

                doubleEscape(chars, offset + lineIndex, lastSpaceIndex - lineIndex - 1 - (trailingSpace ? 1 : 0));
                
                if (trailingSpace) {
                    printer.print(new char[] { '\\', ' '});
                }
                
                printer.println();
                lineIndex = lastSpaceIndex;
                prevSpace = false;
                
            } else if (' ' == chars[i + offset] && !prevSpace) {                
                lastSpaceIndex = i + 1;
                prevSpace = true;
                
            } else {
                prevSpace = false;
            }
        }
  
        if (lineIndex < length) {
            lineIndex += printLeadingSpace(chars, offset + lineIndex);
            doubleEscape(chars, offset + lineIndex, length - lineIndex);
        }
    }
    
    public void printInline(char[] chars, int offset, int length) throws IOException {
        for (int i = 0; i < length; i++) {          
            doubleEscape(chars, offset + i, 1);
        }
    }

    protected int printLeadingSpace(char[] chars, int offset) throws IOException {

        if (!printer.isNewLine() || ' ' != chars[offset]) {
            return 0;
        }
                    
        printer.print(new char[] { '\\', ' '});
        return 1;
    }

    protected static final boolean hasTrailingSpace(char[] chars, int offset, int length) {
        return (chars[offset + length - 1] == ' '); 
    }
    
    protected void  doubleEscape(char[] chars, int offset, int length) throws IOException {
        
        int start = 0;

        // escape non-printable characters
        for (int i = 0; i < length; i++) {
            
            char[] escaped = null;

            if (chars[offset + i] == '\\') {                
                escaped = new char[] { '\\', '\\'};
                
            } else if (chars[offset + i] == 0x0) {
                escaped = new char[] { '\\', '0'};

            } else if (chars[offset + i] == 0x9) {
                escaped = new char[] { '\\', 't'};
                
            } else if (chars[offset + i] == 0xa) {
                escaped = new char[] { '\\', 'n'};

            } else if (chars[offset + i] == '\r') {
                escaped = new char[] { '\\', 'r'};

            } else if (chars[offset + i] == 0x07) {
                escaped = new char[] { '\\', 'a'};

            } else if (chars[offset + i] == 0x8) {
                escaped = new char[] { '\\', 'b'};

            } else if (chars[offset + i] == 0xb) {
                escaped = new char[] { '\\', 'v'};

            } else if (chars[offset + i] == 0xc) {
                escaped = new char[] { '\\', 'f'};

            } else if (chars[offset + i] == 0x1b) {
                escaped = new char[] { '\\', 'e'};

            } else if (chars[offset + i] == '"') {
                escaped = new char[] { '\\', '"'};

            } else if (chars[offset + i] == '/') {
                escaped = new char[] { '\\', '/'};

            } else if (chars[offset + i] == 0xa0) {
                escaped = new char[] { '\\', '_'};

            } else if (chars[offset + i] == 0x85) {
                escaped = new char[] { '\\', 'N'};

            } else if (chars[offset + i] == 0x02028) {
                escaped = new char[] { '\\', 'L'};

            } else if (chars[offset + i] == 0x2029) {
                escaped = new char[] { '\\', 'P'};

            } else if (chars[offset + i] < 0x20 
                    || (chars[offset + i] > 0x7e && chars[offset + i] < 0xa0)) {

                final char[] hex = Integer.toHexString(chars[offset + i] | 0x100).substring(1).toCharArray();

                escaped = new char[4];
                escaped[0] = '\\';
                escaped[1] = 'x';
                escaped[2] = hex[0];
                escaped[3] = hex[1];

            } else if ((chars[offset + i] > 0xd7ff && chars[offset + i] < 0xe000)
                    || (chars[offset + i] > 0xfffd && chars[offset + i] < 0x10000)) {

                final char[] hex = Integer.toHexString(chars[offset + i] | 0x10000).substring(1).toCharArray();

                escaped = new char[6];
                escaped[0] = '\\';
                escaped[1] = 'u';
                escaped[2] = hex[0];
                escaped[3] = hex[1];
                escaped[4] = hex[2];
                escaped[5] = hex[3];

            } else {
                continue;
            }
            
            if (escaped != null) {
                // flush previous chars
                if (i > start) {
                    printer.print(chars, offset + start, i - start);
                }
                printer.print(escaped);
                start = i + 1;
            }
        }
        
        // flush remaining chars
        if (length > start) {
            printer.print(chars, offset + start, length - start);
        }
    }
}
