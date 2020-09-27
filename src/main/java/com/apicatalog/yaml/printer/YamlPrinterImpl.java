package com.apicatalog.yaml.printer;

import java.util.ArrayDeque;
import java.util.Deque;

public class YamlPrinterImpl implements YamlPrinter {

    private enum Context { 
                    DOCUMENT,
                    BLOCK_SCALAR,
                    FLOW_PLAIN_SCALAR,
                    FLOW_DOUBLE_QUOTED_SCALAR,
                    FLOW_SINGLE_QUOTED_SCALAR,
                    BLOCK_SEQUENCE,
                    COMPACT_BLOCK_SEQUENCE, 
                    BLOCK_MAPPING_KEY,
                    BLOCK_MAPPING_VALUE,
                    }

    private final IndentedkWriter writer;

    private Deque<Context> context;
    
    public YamlPrinterImpl(final IndentedkWriter writer) {
        this.writer = writer;
        this.context = new ArrayDeque<>(10);
        this.context.push(Context.DOCUMENT);
    }

    @Override
    public YamlPrinter beginBlockScalar(BlockScalarType type, ChompingStyle chomping)  throws YamlPrinterException {
        
        if (Context.BLOCK_SEQUENCE.equals(context.peek()) || Context.COMPACT_BLOCK_SEQUENCE.equals(context.peek())) {
            writer.print('-');
            writer.print(' ');
        }
        
        if (Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            writer.print(' ');
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
    public YamlPrinter endBlockScalar() throws YamlPrinterException {
        
        if (Context.BLOCK_SCALAR.equals(context.peek())) {
           context.pop();
            
        } else {
            throw new YamlPrinterException();
        }
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
            throw new YamlPrinterException();
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
            
        } else {
            newBlock = false;
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
             throw new YamlPrinterException();
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
            
        } else {
            newBlock = false;
        }

        context.push(Context.BLOCK_MAPPING_KEY);
        
        if (newBlock) {
            writer.beginBlock();
        }

        return this;
    }

    @Override
    public YamlPrinter enBlockdMapping() throws YamlPrinterException {
        
        if (Context.BLOCK_MAPPING_KEY.equals(context.peek()) || Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            context.pop();
             
         } else {
             throw new YamlPrinterException();
         }

        endCollection();
        
        return this;
    }

    @Override
    public YamlPrinter beginFlowScalar(FlowScalarType type) throws YamlPrinterException {
        
        beginScalar(false);

        switch (type) {
        case PLAIN:
            context.push(Context.FLOW_PLAIN_SCALAR);
            writer.beginFlow();
            break;
            
        case DOUBLE_QUOTED:
            writer.print('"');
            writer.beginFlow();
            context.push(Context.FLOW_DOUBLE_QUOTED_SCALAR);
            break;
            
        case SINGLE_QUOTED:
            writer.print('\'');
            writer.beginFlow();
            context.push(Context.FLOW_SINGLE_QUOTED_SCALAR);
            break;
        }

        return this;
    }

    @Override
    public YamlPrinter endFlowScalar() throws YamlPrinterException {

        if (Context.FLOW_PLAIN_SCALAR.equals(context.peek())) {
            
            context.pop();
            
        } else if (Context.FLOW_SINGLE_QUOTED_SCALAR.equals(context.peek())) {
            writer.print('\'');
            context.pop();

            
        } else if (Context.FLOW_DOUBLE_QUOTED_SCALAR.equals(context.peek())) {
            writer.print('"');
            context.pop();

            
        } else {
            throw new YamlPrinterException();
        }

        writer.endFlow();
        endScalar();
        
        return this;
    }
    
    @Override
    public YamlPrinter skip() throws YamlPrinterException {
        beginScalar(true);
        endScalar();
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
        }
    }

    protected void endScalar() throws YamlPrinterException {
        
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
        
        // find next single quote
        for (int i=0; i < length; i++) {
            
            final char[] escaped;
            
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
                escaped = null;
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
}