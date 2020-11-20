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
import java.net.URI;
import java.util.ArrayDeque;
import java.util.Deque;

import com.apicatalog.yaml.printer.scalar.DoubleQuotedPrinter;
import com.apicatalog.yaml.printer.scalar.FoldedPrinter;
import com.apicatalog.yaml.printer.scalar.SingleQuotedPrinter;
import com.apicatalog.yaml.writer.YamlCharacters;
import com.apicatalog.yaml.writer.YamlPrintStyle;

public class DefaultYamlPrinter implements YamlPrinter {

    enum Context { 
        DOCUMENT_BEGIN,
        DOCUMENT_END,
        BLOCK_SEQUENCE,
        BLOCK_MAPPING_KEY,
        BLOCK_MAPPING_VALUE,
        }

    protected final IndentedPrinter printer;
    protected final YamlPrintStyle style;

    private final Deque<Context> context;
    
    public DefaultYamlPrinter(final IndentedPrinter writer, final YamlPrintStyle style) {
        this.printer = writer;
        this.style = style;
        
        this.context = new ArrayDeque<>(10);
        this.context.push(Context.DOCUMENT_BEGIN);
    }

    protected YamlPrinter beginBlockScalar() throws IOException {
        
        if (Context.BLOCK_SEQUENCE.equals(context.peek())) {
            printer.print('-');
            printer.print(' ');
            
        } else if (Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            printer.print(' ');
            
        } else if (Context.DOCUMENT_BEGIN.equals(context.peek())) {
            context.pop();
            context.push(Context.DOCUMENT_END);
            
        } else {
            throw new IllegalStateException();
        }
        
        return this;
    }
    
    protected YamlPrinter endBlockScalar() throws IOException {
        
        if (Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            context.pop();
            context.push(Context.BLOCK_MAPPING_KEY);
      
        } else if (Context.BLOCK_MAPPING_KEY.equals(context.peek())) {
            printer.print(':');
            context.pop();
            context.push(Context.BLOCK_MAPPING_VALUE);            
        }
        
        return this;
    }
    
    protected void printChomping(char[] chars, int offset, int length)  throws IOException {
        
        int trailingNl = 0;
        
        for (int i = length - 1; i >= 0; i--) {
            if ('\n' != chars[offset + i]) {
                break;
            }
            trailingNl++;
        }
        
        if (trailingNl == 0) {
            // strip

        } else if (trailingNl > 1) {
            printer.print('+'); // keep
        }
        //clip
    }
    
    @Override
    public YamlPrinter printLiteralScalar(char[] chars, int offset, int length)  throws IOException {
                  
        beginBlockScalar();
        
        printer.print('|');

        printChomping(chars, offset, length);

        printer.println();
        
        printer.beginBlock();

        int begin = 0;
        boolean empty = true;
      
        for (int i = 0; i < length; i++) {
          
            if ('\n' == chars[i + offset]) {
                if (!empty) {
                    printer.print(chars, offset + begin, i - begin);
                }
                printer.println();
                begin = i + 1;
                empty = true;
                
            } else {
                empty = empty && (' ' == chars[i + offset]);
            }
        }

        // print remaining
        if (begin < length && !empty) {
            printer.print(chars, offset + begin, length - begin);
        }
        
        printer.endBlock();

        return endBlockScalar();
    }

    @Override
    public YamlPrinter printFoldedScalar(char[] chars, int offset, int length)  throws IOException {

        beginBlockScalar();
        
        printer.print('>');
        
        printChomping(chars, offset, length);

        printer.println();
        
        printer.beginBlock();

        (new FoldedPrinter(printer)).print(style.getMaxLineLength() - printer.indentation(), chars, offset, length);

        printer.endBlock();
        
        return endBlockScalar();
    }
    
    @Override
    public final YamlPrinter printScalar(char[] chars, int offset, int length) throws IOException {
        
        final int maxLineLength = style.getMaxLineLength() - printer.indentation();
        
        boolean includesControl = false;
        
        int wsCount = 0;
        int nlCount = 0;
        
        int nlLastIndex = 0;
        int nlMaxDistance = -1;
        
        for (int i=0; i < length; i++) {
            
            char ch = chars[i + offset];
            
            if (YamlCharacters.IS_PRINTABLE.negate().test(ch)) {
                return printDoubleQuotedScalar(chars, offset, length);
            }
            
            if (!includesControl) {
                includesControl = YamlCharacters.IS_CONTROL.test(ch);
            }
            
            if ('\n' == ch) {
                nlCount++;
                
                nlMaxDistance = Math.max(nlMaxDistance, i - nlLastIndex);
                nlLastIndex = i;
                
            } else if (' ' == ch) {
                wsCount++;
            }
        }

        if (' ' == chars[offset] || ' ' == chars[offset + length - 1]) {
            return Context.BLOCK_MAPPING_KEY.equals(context.peek()) || nlCount > 0
                    ? printDoubleQuotedScalar(chars, offset, length)
                    : printSingleQuotedScalar(chars, offset, length);
        }
        
        if (wsCount + nlCount > length / 2) {
            return printDoubleQuotedScalar(chars, offset, length);
        }

        if (nlCount == 0 && includesControl && length < maxLineLength 
                    && (Context.BLOCK_MAPPING_VALUE.equals(context.peek())
                            || Context.BLOCK_SEQUENCE.equals(context.peek())
                            || Context.DOCUMENT_END.equals(context.peek())
                            )) {

            return isURI(chars, offset, length)
                    ?printPlainScalar(chars, offset, length)
                    : printSingleQuotedScalar(chars, offset, length);                
        }

        if (isDocumentContext()) {
            
            if (nlCount == 0 && length < maxLineLength) {
                return includesControl
                            ? printSingleQuotedScalar(chars, offset, length)
                            : printPlainScalar(chars, offset, length);
            }
                                        
            if (nlMaxDistance <= maxLineLength) {
                if (includesControl) {
                    return nlCount > 1 ? printLiteralScalar(chars, offset, length) : printSingleQuotedScalar(chars, offset, length);
                }  
                return printPlainScalar(chars, offset, length);
            }
            return printFoldedScalar(chars, offset, length);
        }
        
        if (nlCount == 0 && (length < maxLineLength || Context.BLOCK_MAPPING_KEY.equals(context.peek()))) {
            return includesControl
                        ? printSingleQuotedScalar(chars, offset, length)
                        : printPlainScalar(chars, offset, length);
        }
                
        if (!Context.BLOCK_MAPPING_KEY.equals(context.peek())) {

            if (nlMaxDistance <= maxLineLength) {
                return nlCount > 1 ? printLiteralScalar(chars, offset, length) : printSingleQuotedScalar(chars, offset, length);

            }
            return printFoldedScalar(chars, offset, length);
        }

        return printDoubleQuotedScalar(chars, offset, length);
    }

    @Override
    public YamlPrinter beginBlockSequence() throws IOException {
        
        final boolean newBlock;
        if (Context.BLOCK_SEQUENCE.equals(context.peek())) {
            printer.print('-');
            
            if (style.isCompactArrays()) {
                printer.print(' ');
                
            } else {
                printer.println();  
            }

            newBlock = true;
            
        } else if (Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            printer.println();
            newBlock = true;
            
        } else if (Context.DOCUMENT_BEGIN.equals(context.peek())) {
            context.pop();
            context.push(Context.DOCUMENT_END);
            newBlock = false;
            
        } else {
            throw new IllegalStateException();
        }
        
        context.push(Context.BLOCK_SEQUENCE);
        
        if (newBlock) {
            printer.beginBlock();
        }
        return this;
    }

    @Override
    public YamlPrinter endBlockSequence() {
        
        if (Context.BLOCK_SEQUENCE.equals(context.peek())) {
            context.pop();
             
         } else {
             throw new IllegalStateException();
         }
        
        endCollection();
        
        return this;
    }

    @Override
    public YamlPrinter beginBlockMapping() throws IOException {
        
        final boolean newBlock;

        if (Context.BLOCK_SEQUENCE.equals(context.peek())) {
            printer.print('-');

            if (style.isCompactArrays()) {
                printer.print(' ');
                
            } else {
                printer.println();  
            }

            newBlock = true;
            
        } else if (Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            printer.println();
            newBlock = true;
            
        } else if (Context.DOCUMENT_BEGIN.equals(context.peek())) {
            context.pop();
            context.push(Context.DOCUMENT_END);
            newBlock = false;
            
        } else {
            throw new IllegalStateException();
        }

        context.push(Context.BLOCK_MAPPING_KEY);
        
        if (newBlock) {
            printer.beginBlock();
        }

        return this;
    }

    @Override
    public YamlPrinter endBlockdMapping() {
        
        if (Context.BLOCK_MAPPING_KEY.equals(context.peek()) || Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            context.pop();
             
         } else {
             throw new IllegalStateException();
         }

        endCollection();
        
        return this;
    }

    @Override
    public YamlPrinter printDoubleQuotedScalar(char[] chars, int offset, int length) throws IOException {

        beginFlowScalar(false);

        printer.print('"');
        printer.beginFlow();

        if (Context.BLOCK_MAPPING_KEY.equals(context.peek())) {
            (new DoubleQuotedPrinter(printer)).printInline(chars, offset, length);

        } else {
            (new DoubleQuotedPrinter(printer)).printFolded(style.getMaxLineLength() - printer.indentation(), chars, offset, length);
        }

        printer.print('"');
        printer.endFlow();

        
        return endFlowScalar();
    }

    @Override
    public YamlPrinter printSingleQuotedScalar(char[] chars, int offset, int length) throws IOException {
        
        beginFlowScalar(false);

        printer.print('\'');
        printer.beginFlow();
        
        (new SingleQuotedPrinter(printer)).print(style.getMaxLineLength() - printer.indentation(), chars, offset, length);
        
        printer.print('\'');
        printer.endFlow();
        
        return endFlowScalar();
    }

    @Override
    public YamlPrinter printPlainScalar(char[] chars, int offset, int length) throws IOException {
        
        beginFlowScalar(false);

        printer.beginFlow();
  
        final int maxLineLength = Math.max(0, style.getMaxLineLength() - printer.indentation());
        
        int lineIndex = 0;
        int lastSpaceIndex = 0;

        for (int i = 0; i < length; i++) {
          
            if ('\n' == chars[i + offset]) {
                
                if (i > lineIndex) {
                    printer.print(chars, offset + lineIndex, i - lineIndex);
                }

                printer.println();
                printer.println();
                
                lineIndex = i + 1;
                lastSpaceIndex = i + 1;
                
            } else if (' ' == chars[i + offset]) {          
                lastSpaceIndex = i + 1;                                    
      
            } else if (i - lineIndex >=  maxLineLength && lastSpaceIndex - lineIndex > 1) {
                printer.print(chars, offset + lineIndex, lastSpaceIndex - lineIndex - 1);
                printer.println();
                lineIndex = lastSpaceIndex;
            }
        }
  
        if (lineIndex < length) {
            printer.print(chars, offset + lineIndex, length - lineIndex);
        }        
        
        printer.endFlow();
        
        return endFlowScalar();
    }

    @Override
    public YamlPrinter printNull() throws IOException {

        if (Context.BLOCK_MAPPING_KEY.equals(context.peek())) {
            throw new IllegalStateException();
        }

        beginFlowScalar(true);
        endFlowScalar();
        return this;
    }
    
    protected void beginFlowScalar(boolean empty) throws IOException {

        if (Context.BLOCK_SEQUENCE.equals(context.peek())) {
            printer.print('-');
            if (!empty) {
                printer.print(' ');
            }
            
        } else if (!empty && Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            printer.print(' ');

        } else if (Context.DOCUMENT_BEGIN.equals(context.peek())) {
            context.pop();
            context.push(Context.DOCUMENT_END);
            
        } else if (!Context.BLOCK_MAPPING_KEY.equals(context.peek())
                && !Context.BLOCK_MAPPING_VALUE.equals(context.peek())
                ) {
            
            throw new IllegalStateException();
        }
            
    }

    protected YamlPrinter endFlowScalar() throws IOException {
        
        if (Context.BLOCK_SEQUENCE.equals(context.peek())) {
            printer.println();
            
        } else if (Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            printer.println();
            context.pop();
            context.push(Context.BLOCK_MAPPING_KEY);
            
        } else if (Context.BLOCK_MAPPING_KEY.equals(context.peek())) {
            printer.print(':');
            context.pop();
            context.push(Context.BLOCK_MAPPING_VALUE);            
        }
        
        return this;
    }

    protected void endCollection() {

        if (Context.BLOCK_SEQUENCE.equals(context.peek())) {
            printer.endBlock();
            
        } else if (Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            printer.endBlock();
            context.pop();
            context.push(Context.BLOCK_MAPPING_KEY);            
        }
    }

    @Override
    public void close() throws IOException {
        printer.close();
    }
    
    private final boolean isDocumentContext() {
        return Context.DOCUMENT_BEGIN.equals(context.peek())
                || Context.DOCUMENT_END.equals(context.peek());
    }
    
    private static final boolean isURI(char[] chars, int offset, int length) {

        // just fragment?
        if (chars[offset] == '#') {
            return false;
        }
        
        try {
            
            return URI.create(String.valueOf(chars, offset, length)) != null;
            
        } catch (IllegalArgumentException e) {
            // ignored
        }
        return false;
    }

}