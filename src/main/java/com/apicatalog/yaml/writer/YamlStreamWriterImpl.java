package com.apicatalog.yaml.writer;

import java.io.PrintWriter;

public class YamlStreamWriterImpl implements YamlStreamWriter {

    private enum State { INIT, END_OF_LINE };
    
    private final PrintWriter writer;
    
    private State state;
    
    public YamlStreamWriterImpl(final PrintWriter writer) {
        this.writer = writer;
        this.state = State.INIT;
    }
    
    @Override
    public void writeLiteralScalar(ChompingStyle chomping) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void writeFoldedScalar(ChompingStyle chomping) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void writePlainScalar(String value) {
        if (state == State.END_OF_LINE) {
            writer.println();            
        } else {
            state = State.END_OF_LINE;
        }
        writer.print(value);
    }

    @Override
    public void writeQuotedScalar(QuotesStyle style, String value) {
        // TODO Auto-generated method stub   
    }
}
