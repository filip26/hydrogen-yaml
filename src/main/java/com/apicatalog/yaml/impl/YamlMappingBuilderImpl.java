package com.apicatalog.yaml.impl;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.apicatalog.yaml.YamlMapping;
import com.apicatalog.yaml.YamlMappingBuilder;
import com.apicatalog.yaml.YamlNode;
import com.apicatalog.yaml.YamlSequenceBuilder;

public class YamlMappingBuilderImpl implements YamlMappingBuilder {

    private final Map<String, YamlNode> mapping;
    
    public YamlMappingBuilderImpl() {
        this(new LinkedHashMap<>());
    }

    protected YamlMappingBuilderImpl(Map<String, YamlNode> mapping) {
        this.mapping = mapping;
    }
    
    public static final YamlMappingBuilder of(Map<String, YamlNode> mapping) {
        return new YamlMappingBuilderImpl(new LinkedHashMap<>(mapping));
    }

    @Override
    public YamlMappingBuilder add(String key, YamlNode value) {
        mapping.put(key, value);
        return this;
    }

    @Override
    public YamlMappingBuilder add(String key, YamlMappingBuilder value) {
        mapping.put(key, value.build());
        return this;
    }

    @Override
    public YamlMappingBuilder add(String key, YamlSequenceBuilder value) {
        mapping.put(key, value.build());
        return this;
    }

    @Override
    public YamlMapping build() {
        if (mapping == null || mapping.isEmpty()) {
            return new YamlMappingImpl(Collections.<String, YamlNode>emptyMap());
        }
        return new YamlMappingImpl(Collections.unmodifiableMap(mapping));
    }
}
