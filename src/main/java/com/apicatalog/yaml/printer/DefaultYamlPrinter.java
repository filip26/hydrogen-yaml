package com.apicatalog.yaml.printer;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;

public class DefaultYamlPrinter implements YamlPrinter {

    private final IndentedkPrinter writer;

    private Deque<Context> context;
    
    public DefaultYamlPrinter(final IndentedkPrinter writer) {
        this.writer = writer;
        this.context = new ArrayDeque<>(10);
        this.context.push(Context.DOCUMENT_BEGIN);
    }

    @Override
    public YamlPrinter beginBlockScalar(BlockScalarType type, ChompingStyle chomping)  throws YamlPrinterException {
        
        if (Context.BLOCK_SEQUENCE.equals(context.peek()) || Context.COMPACT_BLOCK_SEQUENCE.equals(context.peek())) {
            writer.print('-');
            writer.print(' ');
            
        } else if (Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            writer.print(' ');
            
        } else if (Context.DOCUMENT_BEGIN.equals(context.peek())) {
            context.pop();
            context.push(Context.DOCUMENT_END);
            
        } else {
            throw new IllegalStateException();
        }
        
        if (BlockScalarType.FOLDED.equals(type)) {
            writer.print('>');
            
        } else if (BlockScalarType.LITERAL.equals(type)) {
            writer.print('|');
        }
        
        writer.beginBlock();
        
        switch (chomping) {
        case CLIP:
            break;
        case KEEP:
            writer.print('+');
            break;
        case STRIP:
            writer.print('-');
            break;
        }
        writer.newLine();
        context.push(Context.BLOCK_SCALAR);
        return this;
    }
    
    @Override
    public YamlPrinter endScalar() throws YamlPrinterException {

        if (Context.BLOCK_SCALAR.equals(context.peek())) {
           context.pop();

           writer.endBlock();
           
           if (Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
               context.pop();
               context.push(Context.BLOCK_MAPPING_KEY);
               
           } else if (Context.BLOCK_MAPPING_KEY.equals(context.peek())) {
               writer.print(':');
               context.pop();
               context.push(Context.BLOCK_MAPPING_VALUE);            
           }
           
           return this;

        } else if (Context.FLOW_PLAIN_SCALAR.equals(context.peek())) {

        } else if (Context.FLOW_SINGLE_QUOTED_SCALAR.equals(context.peek())) {
            writer.print('\'');
               
        } else if (Context.FLOW_DOUBLE_QUOTED_SCALAR.equals(context.peek())) {
            writer.print('"');
               
       } else {
               throw new IllegalStateException();
       }

       context.pop();
       writer.endFlow();
       
       doEndScalar();

       return this;
    }

    @Override
    public YamlPrinter print(char[] chars, int offset, int length) throws YamlPrinterException {
        
        if (Context.BLOCK_SCALAR.equals(context.peek())) {

            writer.print(chars, offset, length);
            
        } else if (Context.FLOW_PLAIN_SCALAR.equals(context.peek())) {
            
            writer.print(chars, offset, length);
            
        } else if (Context.FLOW_SINGLE_QUOTED_SCALAR.equals(context.peek())) {
            
            singleEscape(chars, offset, length);
            
        } else if (Context.FLOW_DOUBLE_QUOTED_SCALAR.equals(context.peek())) {
            
            doubleEscape(chars, offset, length);

        } else {
            throw new IllegalStateException();
        }
        
        return this;
    }
    
    @Override
    public YamlPrinter beginBlockSequence(boolean compacted) throws YamlPrinterException {
        
        final boolean newBlock;
        
        if (Context.BLOCK_SEQUENCE.equals(context.peek())) {
            writer.print('-');
            writer.newLine();
            newBlock = true;
            
        } else if (Context.COMPACT_BLOCK_SEQUENCE.equals(context.peek())) {
            writer.print('-');
            writer.print(' ');    
            newBlock = true;        

        } else if (Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            writer.newLine();
            newBlock = true;
            
        } else if (Context.DOCUMENT_BEGIN.equals(context.peek())) {
            context.pop();
            context.push(Context.DOCUMENT_END);
            newBlock = false;
            
        } else {
            throw new IllegalStateException();
        }
        
        context.push(compacted ? Context.COMPACT_BLOCK_SEQUENCE : Context.BLOCK_SEQUENCE);
        
        if (newBlock) {
            writer.beginBlock();
        }
        return this;
    }

    @Override
    public YamlPrinter endBlockSequence() throws YamlPrinterException {
        
        if (Context.BLOCK_SEQUENCE.equals(context.peek()) || Context.COMPACT_BLOCK_SEQUENCE.equals(context.peek())) {
            context.pop();
             
         } else {
             throw new IllegalStateException();
         }
        
        endCollection();
        
        return this;
    }

    @Override
    public YamlPrinter beginBlockMapping() throws YamlPrinterException {
        
        final boolean newBlock;

        if (Context.BLOCK_SEQUENCE.equals(context.peek())) {
            writer.print('-');
            writer.newLine();
            newBlock = true;
        } else if (Context.COMPACT_BLOCK_SEQUENCE.equals(context.peek())) {
            writer.print('-');
            writer.print(' ');
            newBlock = true;
            
        } else if (Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            writer.newLine();
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
            writer.beginBlock();
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
    public YamlPrinter beginDoubleQuotedScalar() throws YamlPrinterException {
        
        beginScalar(false);
        writer.print('"');
        writer.beginFlow();
        context.push(Context.FLOW_DOUBLE_QUOTED_SCALAR);

        return this;
    }

    @Override
    public YamlPrinter beginSingleQuotedScalar() throws YamlPrinterException {
        
        beginScalar(false);

        writer.print('\'');
        writer.beginFlow();
        context.push(Context.FLOW_SINGLE_QUOTED_SCALAR);

        return this;
    }

    @Override
    public YamlPrinter beginPlainScalar() throws YamlPrinterException {
        
        beginScalar(false);

        context.push(Context.FLOW_PLAIN_SCALAR);
        writer.beginFlow();

        return this;
    }

    @Override
    public YamlPrinter printNull() throws YamlPrinterException {

        if (Context.BLOCK_MAPPING_KEY.equals(context.peek())) {
            throw new IllegalStateException();
        }
                
        beginScalar(true);
        doEndScalar();
        return this;
    }
    
    protected void beginScalar(boolean empty) throws YamlPrinterException {

        if (Context.BLOCK_SEQUENCE.equals(context.peek()) || Context.COMPACT_BLOCK_SEQUENCE.equals(context.peek())) {
            writer.print('-');
            if (!empty) {
                writer.print(' ');
            }
            
        } else if (!empty && Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            writer.print(' ');
            
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

    protected void doEndScalar() throws YamlPrinterException {
        
        if (Context.BLOCK_SEQUENCE.equals(context.peek()) || Context.COMPACT_BLOCK_SEQUENCE.equals(context.peek())) {
            writer.newLine();
            
        } else if (Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            writer.newLine();
            context.pop();
            context.push(Context.BLOCK_MAPPING_KEY);
            
        } else if (Context.BLOCK_MAPPING_KEY.equals(context.peek())) {
            writer.print(':');
            context.pop();
            context.push(Context.BLOCK_MAPPING_VALUE);            
        }
    }

    protected void endCollection() {

        if (Context.BLOCK_SEQUENCE.equals(context.peek()) || Context.COMPACT_BLOCK_SEQUENCE.equals(context.peek())) {
            writer.endBlock();
            
        } else if (Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            writer.endBlock();
            context.pop();
            context.push(Context.BLOCK_MAPPING_KEY);            
        }
    }

    protected void  doubleEscape(char[] chars, int offset, int length) throws YamlPrinterException {
        int start = 0;

        boolean startSpaces = true; 
        
        // escape non-printable characters
        for (int i = 0; i < length; i++) {
            
            char[] escaped = null;
            
            if (startSpaces) {
                startSpaces = chars[offset + i] == 0x20;
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
                    writer.print(chars, offset + start, i - start);
                }
                writer.print(escaped);
                start = i + 1;
            }
        }
        
        // flush previous chars
        if (length > start) {
            writer.print(chars, offset + start, length - start);
        }
    }

    protected void singleEscape(final char[] chars, final int offset, final int length) throws YamlPrinterException {
        
        int start = 0;
        
        // find next single quote
        for (int i=0; i < length; i++) {
            if (chars[offset + i] == '\'') {
                
                // flush previous chars
                if (i >= start) {
                    writer.print(chars, offset + start, i - start + 1);
                }
                writer.print('\'');
                start = i + 1;
            }
        }
        // flush previous chars
        if (length > start) {
            writer.print(chars, offset + start, length - start);
        }
    }

    @Override
    public YamlPrinter println() throws YamlPrinterException {
        writer.newLine();
        return this;
    }
    
    @Override
    public Collection<Context> getContext() {
        return Collections.unmodifiableCollection(context);
    }

    @Override
    public int indentation() {
        return writer.indentation();
    }
}