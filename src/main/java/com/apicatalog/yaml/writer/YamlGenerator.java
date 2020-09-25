package com.apicatalog.yaml.writer;

public interface YamlGenerator {

    YamlGenerator beginBlockScalar(BlockScalarType type, ChompingStyle chomping);
    
    YamlGenerator writeBlockScalar(String value);
    
    YamlGenerator endBlockScalar();
    
    YamlGenerator writeFlowScalar(FlowScalarType type, String value);

    YamlGenerator beginSequence();

    YamlGenerator endSequence();

    YamlGenerator beginMapping();
    
    YamlGenerator endMapping();    
}
