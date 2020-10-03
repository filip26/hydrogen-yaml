package com.apicatalog.yaml.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.apicatalog.yaml.YamlMappingBuilder;
import com.apicatalog.yaml.YamlNode;
import com.apicatalog.yaml.YamlSequence;
import com.apicatalog.yaml.YamlSequenceBuilder;

public class YamlSequenceBuilderImpl implements YamlSequenceBuilder {

    private final ArrayList<YamlNode> items;
    
    public YamlSequenceBuilderImpl() {
        this(new ArrayList<>());
    }

    protected YamlSequenceBuilderImpl(final ArrayList<YamlNode> nodes) {
        this.items = nodes; 
    }
    
    public static final YamlSequenceBuilder of(final List<YamlNode> nodes) {
        return new YamlSequenceBuilderImpl(new ArrayList<>(nodes));
    }

    @Override
    public YamlSequenceBuilder add(YamlNode node) {
        items.add(node);
        return this;
    }

    @Override
    public YamlSequenceBuilder addNull() {
        items.add(YamlNode.NULL);
        return this;
    }

    @Override
    public YamlSequenceBuilder add(YamlMappingBuilder builder) {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public YamlSequenceBuilder add(YamlSequenceBuilder builder) {
        items.addAll(builder.build());
        return null;
    }

    @Override
    public YamlSequence build() {
        if (items == null || items.isEmpty()) {
            return new YamlSequenceImpl(Collections.emptyList());
        }

        return new YamlSequenceImpl(Collections.unmodifiableList(items));
    }
}
