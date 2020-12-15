/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.apicatalog.yaml.provider;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.apicatalog.yaml.node.YamlMapping;
import com.apicatalog.yaml.node.YamlNode;
import com.apicatalog.yaml.node.builder.YamlMappingBuilder;
import com.apicatalog.yaml.node.builder.YamlSequenceBuilder;

final class YamlMappingBuilderImpl implements YamlMappingBuilder {

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
