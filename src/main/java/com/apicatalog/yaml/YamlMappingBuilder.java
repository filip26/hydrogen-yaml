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
package com.apicatalog.yaml;

public interface YamlMappingBuilder {

    
    default YamlMappingBuilder add(String key, String scalar) {
        return add(key, Yaml.createScalar(scalar));
    }
    
    YamlMappingBuilder add(String key, YamlNode value);
    
    default YamlMappingBuilder addNull(String key) {
        return add(key, YamlNode.NULL);
    }
    
    YamlMappingBuilder add(String key, YamlMappingBuilder value);
    
    YamlMappingBuilder add(String key, YamlSequenceBuilder value);

    default YamlMappingBuilder remove(YamlNode key) {
        throw new UnsupportedOperationException();
    }

    YamlMapping build();
}
