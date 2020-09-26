package com.apicatalog.yaml.generator;

public interface YamlGenerator {

    YamlGenerator beginBlockScalar(BlockScalarType type, ChompingStyle chomping) throws YamlGenerationException;
    
    YamlGenerator endBlockScalar() throws YamlGenerationException;
    
    YamlGenerator beginFlowScalar(FlowScalarType type) throws YamlGenerationException;
    
    YamlGenerator endFlowScalar() throws YamlGenerationException;

    YamlGenerator print(String value) throws YamlGenerationException;
    
    YamlGenerator println() throws YamlGenerationException;

    default YamlGenerator beginBlockSequence() throws YamlGenerationException {
        return beginBlockSequence(false);
    }
    
    YamlGenerator beginBlockSequence(boolean compacted) throws YamlGenerationException;

    YamlGenerator endBlockSequence() throws YamlGenerationException;

    YamlGenerator beginBlockMapping() throws YamlGenerationException;
    
    YamlGenerator enBlockdMapping() throws YamlGenerationException;
    
    YamlGenerator skip() throws YamlGenerationException;
}
