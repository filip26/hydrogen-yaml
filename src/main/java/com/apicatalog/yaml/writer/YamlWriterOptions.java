package com.apicatalog.yaml.writer;

import com.apicatalog.yaml.printer.style.DefaultYamlPrintStyle;
import com.apicatalog.yaml.printer.style.YamlPrintStyle;

public final class YamlWriterOptions {

    private int maxLineWidth;
    private boolean compactArrays;
    
    private YamlPrintStyle style;
    
    public YamlWriterOptions() {
        this.maxLineWidth = 100;
        this.compactArrays = true;
        this.style = DefaultYamlPrintStyle.INSTANCE;
    }
    
    public int getMaxLineWidth() {
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
    public YamlPrintStyle getStyle() {
        return style;
    }
    public void setStyle(YamlPrintStyle style) {
        this.style = style;
    }   
}
