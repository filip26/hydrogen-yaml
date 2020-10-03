package com.apicatalog.yaml.impl;

import java.util.AbstractList;
import java.util.List;

import com.apicatalog.yaml.YamlMapping;
import com.apicatalog.yaml.YamlNode;
import com.apicatalog.yaml.YamlScalar;
import com.apicatalog.yaml.YamlSequence;

public class YamlSequenceImpl extends AbstractList<YamlNode> implements YamlSequence {

    private final List<YamlNode> items;
    
    public YamlSequenceImpl(final List<YamlNode> items) {
        this.items = items;
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.SEQUENCE;
    }

    @Override
    public YamlMapping getMapping(int index) {
        return items.get(index).asMapping();
    }

    @Override
    public YamlSequence getSequence(int index) {
        return items.get(index).asSequence();
    }

    @Override
    public YamlScalar getScalar(int index) {
        return items.get(index).asScalar();
    }

    @Override
    public boolean isNull(int index) {
        return YamlNode.NodeType.NULL.equals(items.get(index).getNodeType());
    }

    @Override
    public YamlNode get(int index) {
        return items.get(index);
    }

    @Override
    public int size() {
        return items.size();
    }
}
