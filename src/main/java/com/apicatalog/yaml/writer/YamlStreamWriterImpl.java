package com.apicatalog.yaml.writer;

import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Deque;

public class YamlStreamWriterImpl implements YamlGenerator {

    private enum Context { 
                    BLOCK_SCALAR,
                    BLOCK_SCALAR_NEXT,
                    BLOCK_SEQUENCE,
                    BLOCK_SEQUENCE_NEXT, 
                    };
    
    private final PrintWriter writer;

    private int indentation;
    
    private Deque<Context> context;
    
    public YamlStreamWriterImpl(final PrintWriter writer) {
        this.writer = writer;
        this.context = new ArrayDeque<>(10);
        this.indentation = 0;
    }
    
    @Override
    public YamlGenerator beginBlockScalar(BlockScalarType type, ChompingStyle chomping) {
        
        if (Context.BLOCK_SEQUENCE.equals(context.peek())) {
            writeIndentation();
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
        writer.println();
        context.push(Context.BLOCK_SCALAR);
        return this;
    }

    @Override
    public YamlGenerator writeBlockScalar(String value) {
        
        if (Context.BLOCK_SCALAR_NEXT.equals(context.peek())) {
            writer.println();          
            
        } else if (Context.BLOCK_SCALAR.equals(context.peek())) {
            context.pop();
            context.push(Context.BLOCK_SCALAR_NEXT);
            
        } else {
            //TODO error
        }
        
        if (value != null && !value.isBlank()) {
            writeIndentation();
            writer.print(' ');
            writer.print(value);
        }
        return this;
    }

    @Override
    public YamlGenerator endBlockScalar() {
        if (Context.BLOCK_SCALAR_NEXT.equals(context.peek()) || Context.BLOCK_SCALAR.equals(context.peek())) {
           context.pop();
            
        } else {
            //TODO error
        }
        return this;
    }
    
    @Override
    public YamlGenerator beginMapping() {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public YamlGenerator beginSequence() {
        context.push(Context.BLOCK_SEQUENCE);
        return this;
    }

    @Override
    public YamlGenerator endSequence() {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public YamlGenerator endMapping() {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public YamlGenerator writeFlowScalar(FlowScalarType type, String value) {

        if (Context.BLOCK_SEQUENCE.equals(context.peek())) {
            writeIndentation();
            writer.print('-');
            writer.print(' ');
        }
        
        if (FlowScalarType.PLAIN.equals(type)) {
            writer.print(value);
        }

        if (Context.BLOCK_SEQUENCE.equals(context.peek())) {
            writer.println();
        }

        return this;
    }
    
    private final void writeIndentation() {
        for (int i=0; i < indentation; i++) {
            writer.print(' ');
            writer.print(' ');
        }
    }
    
}
