package com.apicatalog.yaml.writer;

public interface YamlStreamWriter {

    void writeLiteralScalar(ChompingStyle chomping);
    
    void writeFoldedScalar(ChompingStyle chomping);
    
    void writePlainScalar(String value);
    
    void writeQuotedScalar(QuotesStyle style, String value);

    
}
