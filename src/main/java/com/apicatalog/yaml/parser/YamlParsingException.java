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
package com.apicatalog.yaml.parser;

import com.apicatalog.yaml.YamlException;

public class YamlParsingException extends YamlException {

    private static final long serialVersionUID = 706265442300521912L;
    
    private final YamlLocation location;

    public YamlParsingException(String message, YamlLocation location) {
        super(message);
        this.location = location;
    }

    public YamlParsingException(String message, Throwable cause, YamlLocation location) {
        super(message, cause);
        this.location = location;
    }
    
    public YamlLocation getLocation() {
        return location;
    }
}