package com.apicatalog.yaml.generator;

import java.util.ArrayDeque;
import java.util.Deque;

public class YamlGeneratorImpl implements YamlGenerator {

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
    
    public YamlGeneratorImpl(final IndentedkWriter writer) {
        this.writer = writer;
        this.context = new ArrayDeque<>(10);
        this.context.push(Context.DOCUMENT);
    }

    @Override
    public YamlGenerator beginBlockScalar(BlockScalarType type, ChompingStyle chomping)  throws YamlGenerationException {
        
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
    public YamlGenerator endBlockScalar() throws YamlGenerationException {
        
        if (Context.BLOCK_SCALAR.equals(context.peek())) {
           context.pop();
            
        } else {
            throw new YamlGenerationException();
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
    public YamlGenerator print(String value) throws YamlGenerationException {
        
        if (value.isBlank()) {
            return this;
        }
        
        if (Context.BLOCK_SCALAR.equals(context.peek())) {

            writer.print(value);
            
        } else if (Context.FLOW_PLAIN_SCALAR.equals(context.peek())) {
            
            writer.print(value);
            
        } else if (Context.FLOW_SINGLE_QUOTED_SCALAR.equals(context.peek())) {
            
            writer.print(escape(FlowScalarType.SINGLE_QUOTED, value));
            
        } else if (Context.FLOW_DOUBLE_QUOTED_SCALAR.equals(context.peek())) {
            
            writer.print(escape(FlowScalarType.DOUBLE_QUOTED, value));
            
        } else {
            throw new YamlGenerationException();
        }
        
        return this;
    }
    
    @Override
    public YamlGenerator beginBlockSequence(boolean compacted) throws YamlGenerationException {
        
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
    public YamlGenerator endBlockSequence() throws YamlGenerationException {
        
        if (Context.BLOCK_SEQUENCE.equals(context.peek()) || Context.COMPACT_BLOCK_SEQUENCE.equals(context.peek())) {
            context.pop();
             
         } else {
             throw new YamlGenerationException();
         }
        
        endCollection();
        
        return this;
    }

    @Override
    public YamlGenerator beginBlockMapping() throws YamlGenerationException {
        
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
    public YamlGenerator enBlockdMapping() throws YamlGenerationException {
        
        if (Context.BLOCK_MAPPING_KEY.equals(context.peek()) || Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            context.pop();
             
         } else {
             throw new YamlGenerationException();
         }

        endCollection();
        
        return this;
    }

    @Override
    public YamlGenerator beginFlowScalar(FlowScalarType type) throws YamlGenerationException {
        
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
    public YamlGenerator endFlowScalar() throws YamlGenerationException {

        if (Context.FLOW_PLAIN_SCALAR.equals(context.peek())) {
            
            context.pop();
            
        } else if (Context.FLOW_SINGLE_QUOTED_SCALAR.equals(context.peek())) {
            writer.print('\'');
            context.pop();

            
        } else if (Context.FLOW_DOUBLE_QUOTED_SCALAR.equals(context.peek())) {
            writer.print('"');
            context.pop();

            
        } else {
            throw new YamlGenerationException();
        }

        writer.endFlow();
        endScalar();
        
        return this;
    }
    
    @Override
    public YamlGenerator skip() throws YamlGenerationException {
        beginScalar(true);
        endScalar();
        return this;
    }
    
    protected void beginScalar(boolean empty) throws YamlGenerationException {

        if (Context.BLOCK_SEQUENCE.equals(context.peek()) || Context.COMPACT_BLOCK_SEQUENCE.equals(context.peek())) {
            writer.print('-');
            if (!empty) {
                writer.print(' ');
            }
            
        } else if (!empty && Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            writer.print(' ');
        }
    }

    protected void endScalar() throws YamlGenerationException {
        
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

    protected String escape(FlowScalarType type, String value) {
        //TODO
        return value;
    }

    @Override
    public YamlGenerator println() throws YamlGenerationException {
        writer.newLine();
        return this;
    }
}