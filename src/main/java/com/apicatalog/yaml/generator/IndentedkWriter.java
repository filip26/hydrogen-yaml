package com.apicatalog.yaml.generator;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.Deque;

public final class IndentedkWriter {

    private final Writer writer;
    
    private boolean newLine;
    private int printed;
    
    private static final char[][] SPACES = {
            { ' '},
            { ' ', ' ' },
            { ' ', ' ', ' '},
            { ' ', ' ', ' ', ' ' },
            { ' ', ' ', ' ', ' ', ' ' },
            { ' ', ' ', ' ', ' ', ' ', ' ' },
            { ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' },
            { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' , ' ' },
            { ' ', ' ' , ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            { ' ', ' ' , ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            { ' ', ' ' , ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            { ' ', ' ' , ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            { ' ', ' ' , ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            { ' ', ' ' , ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            { ' ', ' ' , ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            { ' ', ' ' , ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            { ' ', ' ' , ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            { ' ', ' ' , ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            { ' ', ' ' , ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
    };
    
    private Deque<Integer> indentation;
    
    public IndentedkWriter(Writer writer) {
        this.writer = writer;
        this.indentation = new ArrayDeque<>(30);
        this.indentation.push(0);
        this.newLine = false;
        this.printed = 0;
    }
    
    public IndentedkWriter beginBlock() {
        indentation.push(indentation.peek() + 2);
        return this;
    }

    public IndentedkWriter endBlock() {
        indentation.pop();
        newLine = true;
        return this;
    }
    
    public IndentedkWriter beginFlow() {
        indentation.push(newLine ? indentation.peek() : printed);
        return this;
    }
    
    public IndentedkWriter endFlow() {
        indentation.pop();
        return this;        
    }

    public IndentedkWriter newLine() throws YamlGenerationException {

        if (newLine) {
            try {

                writer.write(new char[] {'\n'});
                printed = 0;
                return this;
                
            } catch (IOException e) {
                throw new YamlGenerationException(e);
            }
        }
        
        this.newLine = true;
        return this;
    }
    
    public IndentedkWriter print(char[] chars, int offset, int length)  throws YamlGenerationException {
        
        try {
            if (newLine) {
                printIndentation();
            }
            
            printed += length;
            writer.write(chars, offset, length);
            
        } catch (IOException e) {
            throw new YamlGenerationException(e);
        }
        return this;
    }

    public IndentedkWriter print(char ch)  throws YamlGenerationException {

        try {
            if (newLine) {                
                printIndentation();
            }

            printed += 1;
            writer.write(ch);
            
        } catch (IOException e) {
            throw new YamlGenerationException(e);
        }
        return this;
    }    
    
    private final void printIndentation() throws IOException {
        
        writer.write(new char[] {'\n'});
        printed = indentation.peek();
        newLine = false;

        if (indentation.peek() > 0) {
            
            if (indentation.peek() < 20) {
                writer.write(SPACES[indentation.peek() - 1]);
                
            } else {
                writer.write(SPACES[19]);
                for (int i=0; i < indentation.peek() - 20; i++) {
                    writer.write(' ');
                }
            }
        }        
    }    
}
