package com.apicatalog.yaml.writer;

import com.apicatalog.yaml.printer.style.YamlPrinterStyle;

public class YamlWriterOptions {


    private int maxLineWidth;
    private boolean compactArrays;
    
    private YamlPrinterStyle style;
    
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
    public YamlPrinterStyle getStyle() {
        return style;
    }
    public void setStyle(YamlPrinterStyle style) {
        this.style = style;
    }   
}
