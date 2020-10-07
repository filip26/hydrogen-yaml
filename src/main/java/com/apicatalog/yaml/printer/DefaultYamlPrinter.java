package com.apicatalog.yaml.printer;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

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
//TODO        
//      switch (chomping) {
//      case CLIP:
//          break;
//      case KEEP:
//          printer.print('+');
//          break;
//      case STRIP:
//          printer.print('-');
//          break;
//      }
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
    public YamlPrinter printFoldedScalar(char[] chars, int offset, int length)  throws YamlPrinterException, IOException {

        beginBlockScalar();
        
        printer.print('>');
        
        printChomping(chars, offset, length);

        printer.println();
        
        printer.beginBlock();

        final int maxLineLength = style.getMaxLineLength() - printer.indentation();
        
        int lineIndex = 0;
        int lastSpaceIndex = 0;
      
        for (int i = 0; i < length; i++) {
          
            if ('\n' == chars[i + offset]) {
                
                if (i - lineIndex > 0) {
                    printer.print(chars, offset + lineIndex, i - lineIndex);
                }

                printer.println();
                lineIndex = i + 1;
                lastSpaceIndex = i + 1;
      
            } else if (i - lineIndex >=  maxLineLength) {       
                printer.print(chars, offset + lineIndex, lastSpaceIndex - lineIndex - 1);
                printer.println();
                lineIndex = lastSpaceIndex;                
      
            } else if (' ' == chars[i + offset]) {
          
                lastSpaceIndex = i + 1;                                    
            }
        }
  
        if (lineIndex < length) {
            printer.print(chars, offset + lineIndex, length - lineIndex);
        }

        printer.endBlock();
        
        return endBlockScalar();
    }
    
    @Override
    public final YamlPrinter printScalar(char[] chars, int offset, int length) throws YamlPrinterException, IOException {
        
        final int maxLineLength = style.getMaxLineLength() - printer.indentation();
        
        boolean includesControl = false;
        
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
            }
        }

        if (isDocumentContext()) {
            
            if (nlCount == 0 && !includesControl && length < maxLineLength) {
                return printPlainScalar(chars, offset, length);
            }
            
            if (nlCount == 0 && includesControl && length < maxLineLength) {
                return printSingleQuotedScalar(chars, offset, length);
            }
                            
            if (nlMaxDistance <= maxLineLength) {
                if (!includesControl) {
                    return printPlainScalar(chars, offset, length);
                }
                return printLiteralScalar(chars, offset, length);

            }
            return printFoldedScalar(chars, offset, length);
        }

        if (!includesControl && nlCount == 0 && (length < maxLineLength || Context.BLOCK_MAPPING_KEY.equals(context))) {
            return printPlainScalar(chars, offset, length);
        }
        
        if (includesControl && nlCount == 0 && (length < maxLineLength || Context.BLOCK_MAPPING_KEY.equals(context))) {
            return printSingleQuotedScalar(chars, offset, length);
        }
        
        if (!Context.BLOCK_MAPPING_KEY.equals(context.peek())) {

            if (nlMaxDistance <= maxLineLength) {
                return printLiteralScalar(chars, offset, length);

            }
            return printFoldedScalar(chars, offset, length);
        }

        return printDoubleQuotedScalar(chars, offset, length);
    }

    @Override
    public YamlPrinter beginBlockSequence() throws YamlPrinterException, IOException {
        
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
    public YamlPrinter endBlockSequence() throws YamlPrinterException {
        
        if (Context.BLOCK_SEQUENCE.equals(context.peek())) {
            context.pop();
             
         } else {
             throw new IllegalStateException();
         }
        
        endCollection();
        
        return this;
    }

    @Override
    public YamlPrinter beginBlockMapping() throws YamlPrinterException, IOException {
        
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
    public YamlPrinter endBlockdMapping() throws YamlPrinterException {
        
        if (Context.BLOCK_MAPPING_KEY.equals(context.peek()) || Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            context.pop();
             
         } else {
             throw new IllegalStateException();
         }

        endCollection();
        
        return this;
    }

    @Override
    public YamlPrinter printDoubleQuotedScalar(char[] chars, int offset, int length) throws YamlPrinterException, IOException {
        
        //!YamlPrinter.Context.BLOCK_MAPPING_KEY.equals(context)
        
        beginFlowScalar(false);
        printer.print('"');
        printer.beginFlow();

        final int maxLineLength = style.getMaxLineLength() - printer.indentation();
        
        int lineIndex = 0;
        int lastSpaceIndex = 0;
      
        for (int i = 0; i < length; i++) {
          
            if ('\n' == chars[i + offset]) {
                
                if (i - lineIndex > 0) {
                    doubleEscape(chars, offset + lineIndex, i - lineIndex);
                }

                printer.println();
                
                lineIndex = i + 1;
                lastSpaceIndex = i + 1;
      
            } else if (i - lineIndex >=  maxLineLength) {   
                
                doubleEscape(chars, offset + lineIndex, lastSpaceIndex - lineIndex - 1);
                printer.println();
                lineIndex = lastSpaceIndex;                
      
            } else if (' ' == chars[i + offset]) {
          
                lastSpaceIndex = i + 1;                                    
            }
        }
  
        if (lineIndex < length) {
            doubleEscape(chars, offset + lineIndex, length - lineIndex);
        }

        printer.endFlow();
        printer.print('"');
        
        return endFlowScalar();
    }

    @Override
    public YamlPrinter printSingleQuotedScalar(char[] chars, int offset, int length) throws YamlPrinterException, IOException {
        
        beginFlowScalar(false);

        printer.print('\'');
        printer.beginFlow();
        
        final int maxLineLength = style.getMaxLineLength() - printer.indentation();
        
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
      
            } else if (i - lineIndex >=  maxLineLength) {   
                
                singleEscape(chars, offset + lineIndex, lastSpaceIndex - lineIndex - 1);
                printer.println();
                lineIndex = lastSpaceIndex;                
      
            } else if (' ' == chars[i + offset]) {
          
                lastSpaceIndex = i + 1;                                    
            }
        }
  
        if (lineIndex < length) {
            singleEscape(chars, offset + lineIndex, length - lineIndex);
        }
        
        printer.endFlow();
        printer.print('\'');
        
        return endFlowScalar();
    }

    @Override
    public YamlPrinter printPlainScalar(char[] chars, int offset, int length) throws YamlPrinterException, IOException {
        
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
      
            } else if (i - lineIndex >=  maxLineLength && lastSpaceIndex - lineIndex > 0) {
                printer.print(chars, offset + lineIndex, lastSpaceIndex - lineIndex - 1);
                printer.println();
                lineIndex = lastSpaceIndex;

            } else if (' ' == chars[i + offset]) {          
                lastSpaceIndex = i + 1;                                    
            }
        }
  
        if (lineIndex < length) {
            printer.print(chars, offset + lineIndex, length - lineIndex);
        }        
        
        printer.endFlow();
        
        return endFlowScalar();
    }

    @Override
    public YamlPrinter printNull() throws YamlPrinterException, IOException {

        if (Context.BLOCK_MAPPING_KEY.equals(context.peek())) {
            throw new IllegalStateException();
        }

        beginFlowScalar(true);
        endFlowScalar();
        return this;
    }
    
    protected void beginFlowScalar(boolean empty) throws YamlPrinterException, IOException {

        if (Context.BLOCK_SEQUENCE.equals(context.peek())) {
            printer.print('-');
            if (!empty) {
                printer.print(' ');
            }
            
        } else if (!empty && Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            printer.print(' ');
            
        } else if (Context.BLOCK_MAPPING_KEY.equals(context.peek())
                || Context.BLOCK_MAPPING_VALUE.equals(context.peek())
                ) {
            
        } else if (Context.DOCUMENT_BEGIN.equals(context.peek())) {
            context.pop();
            context.push(Context.DOCUMENT_END);
            
        } else {
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

    protected void  doubleEscape(char[] chars, int offset, int length) throws IOException {
        int start = 0;

        boolean startSpaces = true; 
        
        // escape non-printable characters
        for (int i = 0; i < length; i++) {
            
            char[] escaped = null;
            
            if (startSpaces) {
                startSpaces = printer.isNewLine() && chars[offset + i] == 0x20;
            }
            
            if (startSpaces) { 

                escaped = new char[] { '\\', ' '};
            
            } else if (chars[offset + i] == '\\') {
                
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
                if (i >= start) {
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

    protected void singleEscape(final char[] chars, final int offset, final int length) throws YamlPrinterException, IOException {
        
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

    @Override
    public void close() throws IOException {
        printer.close();
    }
    
    private final boolean isDocumentContext() {
        return Context.DOCUMENT_BEGIN.equals(context.peek())
                || Context.DOCUMENT_END.equals(context.peek());
    }

}