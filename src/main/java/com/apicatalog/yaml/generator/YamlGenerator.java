package com.apicatalog.yaml.generator;

public interface YamlGenerator {

    YamlGenerator beginBlockScalar(BlockScalarType type, ChompingStyle chomping) throws YamlGenerationException;
    
    YamlGenerator writeBlockScalar(String value) throws YamlGenerationException;
    
    YamlGenerator endBlockScalar() throws YamlGenerationException;
    
    YamlGenerator writeFlowScalar(FlowScalarType type, String value) throws YamlGenerationException;

    default YamlGenerator beginSequence() throws YamlGenerationException {
        return beginSequence(false);
    }
    
    YamlGenerator beginSequence(boolean compacted) throws YamlGenerationException;

    YamlGenerator endSequence() throws YamlGenerationException;

    YamlGenerator beginMapping() throws YamlGenerationException;
    
    YamlGenerator endMapping() throws YamlGenerationException;
}
