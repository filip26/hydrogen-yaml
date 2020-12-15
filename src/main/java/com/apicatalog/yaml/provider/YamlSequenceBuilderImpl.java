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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.apicatalog.yaml.YamlMappingBuilder;
import com.apicatalog.yaml.YamlSequenceBuilder;
import com.apicatalog.yaml.node.YamlNode;
import com.apicatalog.yaml.node.YamlSequence;

final class YamlSequenceBuilderImpl implements YamlSequenceBuilder {

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
        items.add(builder.build());
        return this;
    }

    @Override
    public YamlSequenceBuilder add(YamlSequenceBuilder builder) {
        items.add(builder.build());
        return this;
    }

    @Override
    public YamlSequence build() {
        if (items == null || items.isEmpty()) {
            return new YamlSequenceImpl(Collections.emptyList());
        }

        return new YamlSequenceImpl(Collections.unmodifiableList(items));
    }
}
