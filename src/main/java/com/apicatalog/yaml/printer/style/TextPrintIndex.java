package com.apicatalog.yaml.printer.style;

public final class TextPrintIndex {

    public static final TextPrintIndex NEW_LINE = new TextPrintIndex(); 
    
    public enum Type { PRINT, PRINT_LN };
    
    private final Type type;
    
    private final int offset;
    private final int length;
    
    public TextPrintIndex(Type type, int offset, int length) {
        this.offset = offset;
        this.length = length;
        this.type = type;
    }

    private TextPrintIndex() {
        this(Type.PRINT_LN, -1, -1);
    }
    
    public int getOffset() {
        return offset;
    }
    
    public int getLength() {
        return length;
    }
    
    public Type getType() {
        return type;
    }
}
