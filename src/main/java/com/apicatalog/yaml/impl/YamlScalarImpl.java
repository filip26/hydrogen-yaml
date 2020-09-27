package com.apicatalog.yaml.impl;

import com.apicatalog.yaml.YamlScalar;

public class YamlScalarImpl implements YamlScalar {

    final String value;
    
    public YamlScalarImpl(final String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public NodeType getNodeType() {
        return NodeType.SCALAR;
    }

    @Override
    public String toString() {
        return "YamlScalarImpl [value=" + value + "]";
    }
}
