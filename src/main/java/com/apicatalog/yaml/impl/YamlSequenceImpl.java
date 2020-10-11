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
