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

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

import com.apicatalog.yaml.node.YamlMapping;
import com.apicatalog.yaml.node.YamlNode;
import com.apicatalog.yaml.node.YamlScalar;
import com.apicatalog.yaml.node.YamlSequence;

final class YamlMappingImpl extends AbstractMap<String, YamlNode> implements YamlMapping {

    private final Map<String, YamlNode> mapping;

    public YamlMappingImpl(final Map<String, YamlNode> mapping) {
        this.mapping = mapping;
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