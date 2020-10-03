package com.apicatalog.yaml.impl;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

import com.apicatalog.yaml.YamlMapping;
import com.apicatalog.yaml.YamlNode;
import com.apicatalog.yaml.YamlScalar;
import com.apicatalog.yaml.YamlSequence;

public class YamlMappingImpl extends AbstractMap<String, YamlNode> implements YamlMapping {

    private final Map<String, YamlNode> mapping;

    public YamlMappingImpl(final Map<String, YamlNode> mapping) {
        this.mapping = mapping;
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.MAPPING;
    }

    @Override
    public YamlSequence getSequence(String key) {
        return mapping.get(key).asSequence();
    }

    @Override
    public YamlMapping getMapping(String key) {
        return mapping.get(key).asMapping();
    }

    @Override
    public YamlScalar getScalar(String key) {
        return mapping.get(key).asScalar();
    }

    @Override
    public boolean isNull(String key) {
        return YamlNode.NodeType.NULL.equals(mapping.get(key).getNodeType());
    }

    @Override
    public Set<Entry<String, YamlNode>> entrySet() {
        return mapping.entrySet();
    }
}