package com.apicatalog.yaml.generator;

import java.io.IOException;
import java.io.Writer;

public final class BlockWriter {

    private final Writer writer;
    
    private int indentation;
    
    private boolean newLine;
    
    private static final char[][] SPACES = {
            { ' ', ' ' },
            { ' ', ' ', ' ', ' ' },
            { ' ', ' ', ' ', ' ', ' ', ' ' },
            { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' },
            { ' ', ' ' , ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            { ' ', ' ' , ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
            { ' ', ' ' , ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}
    };
    
    public BlockWriter(Writer writer) {
        this.writer = writer;
        this.indentation = -1;
        this.newLine = false;
    }
    
    public BlockWriter beginBlock() {
        indentation++;
        return this;
    }

    public BlockWriter endBlock() {
        indentation--;
        this.newLine = true;
        return this;
    }

    public BlockWriter newLine() throws YamlGenerationException {

        if (newLine) {
            try {

                writer.write(new char[] {'\n'});
                return this;
                
            } catch (IOException e) {
                throw new YamlGenerationException(e);
            }
        }
        
        this.newLine = true;
        return this;
    }
    
    public BlockWriter print(String line)  throws YamlGenerationException {
        try {
            if (newLine) {
                writer.write(new char[] {'\n'});
                newLine = false;
                
                if (indentation >= 0) {
                    if (indentation < 7) {
                        writer.write(SPACES[indentation]);
                        
                    } else {
                        writer.write(SPACES[6]);
                        
                        for (int i=0; i < indentation - 6; i++) {
                            writer.write(SPACES[0]);
                        }
                    }
                }
            }
            
            writer.write(line);
            
        } catch (IOException e) {
            throw new YamlGenerationException(e);
        }
        return this;
    }

    public BlockWriter print(char ch)  throws YamlGenerationException {
        try {
            if (newLine) {
                writer.write(new char[] {'\n'});
                newLine = false;

                if (indentation >= 0) {
                    if (indentation < 7) {
                        writer.write(SPACES[indentation]);
                        
                    } else {
                        writer.write(SPACES[6]);
                        
                        for (int i=0; i < indentation - 6; i++) {
                            writer.write(SPACES[0]);
                        }
                    }
                }
            }

            writer.write(ch);
            
        } catch (IOException e) {
            throw new YamlGenerationException(e);
        }
        return this;
    }    
}
