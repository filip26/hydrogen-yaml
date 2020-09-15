package com.apicatalog.yaml.writer;

import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Deque;

public class YamlStreamWriterImpl implements YamlStreamWriter {

    private enum State { 
                    BLOCK_LITERAL_SCALAR, 
                    BLOCK_FOLDED_SCALAR,
                    PLAIN_SCALAR 
                    };
    
    private final PrintWriter writer;

    private Deque<State> state;
    
    public YamlStreamWriterImpl(final PrintWriter writer) {
        this.writer = writer;
        this.state = new ArrayDeque<>(8);
    }
    
    @Override
    public void writeLiteralScalar(ChompingStyle chomping) {
        writer.print('|');
        
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
        state.push(State.BLOCK_LITERAL_SCALAR);
    }

    @Override
    public void writeFoldedScalar(ChompingStyle chomping) {
        writer.print('>');
        
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
        state.push(State.BLOCK_FOLDED_SCALAR);        
    }

    @Override
    public void writePlainScalar(String value) {
        if (State.PLAIN_SCALAR.equals(state.peek())) {
            writer.println();            
        } else {
            state.push(State.PLAIN_SCALAR);
        }
        
        if (value != null && !value.isBlank()) {
            for (int i=0; i < state.size() - 1; i++) {
                writer.print("  ");    
            }
            writer.print(value);
        }
    }

    @Override
    public void writeQuotedScalar(QuotesStyle style, String value) {
        // TODO Auto-generated method stub   
    }
}
