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

final class DoubleQuotedPrinter {

    private final IndentedPrinter printer;
    
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

    private int printLeadingSpace(char[] chars, int offset) throws IOException {

        if (!printer.isNewLine() || ' ' != chars[offset]) {
            return 0;
        }
                    
        printer.print(new char[] { '\\', ' '});
        return 1;
    }

    private static final boolean hasTrailingSpace(char[] chars, int offset, int length) {
        return (chars[offset + length - 1] == ' '); 
    }
    
    private void doubleEscape(char[] chars, int offset, int length) throws IOException {
        
        int start = 0;

        // escape non-printable characters
        for (int i = 0; i < length; i++) {
            
            final char[] escaped = doubleEscape(chars[offset + i]);
            
            if (escaped.length > 0) {
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
    
    private static char[] doubleEscape(char ch) {
                
        for (int i = 0; i < YamlCharacters.DOUBLE_ESCAPE_MAPPING.length; i++) {
            if (YamlCharacters.DOUBLE_ESCAPE_MAPPING[i][0] == ch) {
                return new char[] {'\\', YamlCharacters.DOUBLE_ESCAPE_MAPPING[i][1]};
            }
        }
        
        if (ch < 0x20 || (ch > 0x7e && ch < 0xa0)) {

            final char[] hex = Integer.toHexString(ch | 0x100).substring(1).toCharArray();

            return new char[] { '\\', 'x', hex[0], hex[1]};
        }
        
        if ((ch > 0xd7ff && ch < 0xe000) || (ch > 0xfffd && ch < 0x10000)) {

            final char[] hex = Integer.toHexString(ch | 0x10000).substring(1).toCharArray();

            return new char[] { '\\', 'u', hex[0], hex[1], hex[2], hex[3]};
        }
        
        return new char[] {};
    }    
}
