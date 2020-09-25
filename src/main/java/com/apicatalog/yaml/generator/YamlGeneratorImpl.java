package com.apicatalog.yaml.generator;

import java.util.ArrayDeque;
import java.util.Deque;

public class YamlGeneratorImpl implements YamlGenerator {

    private enum Context { 
                    DOCUMENT,
                    BLOCK_SCALAR,
                    BLOCK_SCALAR_NEXT,
                    BLOCK_SEQUENCE, 
                    BLOCK_MAPPING_KEY,
                    BLOCK_MAPPING_VALUE,
                    };
    
    private final BlockWriter writer;

    private Deque<Context> context;
    
    public YamlGeneratorImpl(final BlockWriter writer) {
        this.writer = writer;
        this.context = new ArrayDeque<>(10);
        this.context.push(Context.DOCUMENT);
    }

    @Override
    public YamlGenerator beginBlockScalar(BlockScalarType type, ChompingStyle chomping)  throws YamlGenerationException {
        
        if (Context.BLOCK_SEQUENCE.equals(context.peek())) {
            writer.print('-');
            writer.print(' ');
        }
        
        if (BlockScalarType.FOLDED.equals(type)) {
            writer.print('>');
            
        } else if (BlockScalarType.LITERAL.equals(type)) {
            writer.print('|');
        }
        
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
    public YamlGenerator writeBlockScalar(String value) throws YamlGenerationException {
        
        if (Context.BLOCK_SCALAR_NEXT.equals(context.peek())) {
            writer.newLine();          
            
        } else if (Context.BLOCK_SCALAR.equals(context.peek())) {
            context.pop();
            context.push(Context.BLOCK_SCALAR_NEXT);
            
        } else {
            throw new YamlGenerationException();
        }
        
        if (value != null && !value.isBlank()) {
            writer.print(' ');
            writer.print(value);
        }
        return this;
    }

    @Override
    public YamlGenerator endBlockScalar() throws YamlGenerationException {
        
        if (Context.BLOCK_SCALAR_NEXT.equals(context.peek()) || Context.BLOCK_SCALAR.equals(context.peek())) {
           context.pop();
            
        } else {
            throw new YamlGenerationException();
        }
        
        endScalar();
        
        return this;
    }
    
    @Override
    public YamlGenerator beginSequence(boolean compacted) throws YamlGenerationException {
        
        final boolean newBlock;
        
        if (Context.BLOCK_SEQUENCE.equals(context.peek())) {
            writer.print('-');
            if (!compacted) {
                writer.newLine();
            }
            newBlock = true;
            
        } else if (Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            if (!compacted) {
                writer.newLine();
            }
            newBlock = true;
            
        } else {
            newBlock = false;
        }
        
        context.push(Context.BLOCK_SEQUENCE);
        
        if (newBlock) {
            writer.beginBlock();
        }
        return this;
    }

    @Override
    public YamlGenerator endSequence() throws YamlGenerationException {
        
        if (Context.BLOCK_SEQUENCE.equals(context.peek())) {
            context.pop();
             
         } else {
             throw new YamlGenerationException();
         }
        
        endCollection();
        
        return this;
    }

    @Override
    public YamlGenerator beginMapping() throws YamlGenerationException {
        
        final boolean newBlock;

        if (Context.BLOCK_SEQUENCE.equals(context.peek())) {
            writer.print('-');
            newBlock = true;
            
        } else if (Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            newBlock = true;
            
        } else {
            newBlock = false;
        }

        if (newBlock) {
            writer.newLine();
        }

        context.push(Context.BLOCK_MAPPING_KEY);
        
        if (newBlock) {
            writer.beginBlock();
        }

        return this;
    }

    @Override
    public YamlGenerator endMapping() throws YamlGenerationException {
        
        if (Context.BLOCK_MAPPING_KEY.equals(context.peek()) || Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            context.pop();
             
         } else {
             throw new YamlGenerationException();
         }

        endCollection();
        
        return this;
    }

    @Override
    public YamlGenerator writeFlowScalar(FlowScalarType type, String value) throws YamlGenerationException {
        
        beginScalar(false);
        
        switch (type) {
        case PLAIN:
            writer.print(value);
            break;
            
        case DOUBLE_QUOTED:
            writer.print('"');
            writer.print(escape(type, value));
            writer.print('"');
            break;
            
        case SINGLE_QUOTED:
            writer.print('\'');
            writer.print(escape(type, value));
            writer.print('\'');
            break;
        }
        
        endScalar();

        return this;
    }

    @Override
    public YamlGenerator writeUndefined() throws YamlGenerationException {
        beginScalar(true);
        endScalar();
        return this;
    }
    
    protected void beginScalar(boolean empty) throws YamlGenerationException {

        if (Context.BLOCK_SEQUENCE.equals(context.peek())) {
            writer.print('-');
            if (!empty) {
                writer.print(' ');
            }
            
        } else if (!empty && Context.BLOCK_MAPPING_VALUE.equals(context.peek())) {
            writer.print(' ');
        }
    }

    protected void endScalar() throws YamlGenerationException {

        if (Context.BLOCK_SEQUENCE.equals(context.peek())) {
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

    protected void endCollection() throws YamlGenerationException {

        if (Context.BLOCK_SEQUENCE.equals(context.peek())) {
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
}
