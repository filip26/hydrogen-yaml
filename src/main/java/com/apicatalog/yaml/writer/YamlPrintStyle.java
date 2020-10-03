package com.apicatalog.yaml.writer;

public final class YamlPrintStyle {

    private int maxLineWidth;
    private boolean compactArrays;
    
    public YamlPrintStyle() {
        this.maxLineWidth = 100;
        this.compactArrays = true;
    }
    
    public int getMaxLineLength() {
        return maxLineWidth;
    }
    public void setMaxLineWidth(int maxLineWidth) {
        this.maxLineWidth = maxLineWidth;
    }
    public boolean isCompactArrays() {
        return compactArrays;
    }
    public void setCompactArrays(boolean compactArrays) {
        this.compactArrays = compactArrays;
    }   
}
