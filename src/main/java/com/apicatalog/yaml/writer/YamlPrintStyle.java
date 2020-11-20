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
package com.apicatalog.yaml.writer;

public final class YamlPrintStyle {

    private int maxLineWidth;
    private boolean compactArrays;
    
    public YamlPrintStyle() {
        this.maxLineWidth = 100;
        this.compactArrays = true;
    }
    
    public int getMaxLineLength() {
        return maxLineWidth;
    }
    public void setMaxLineWidth(int maxLineWidth) {
        this.maxLineWidth = maxLineWidth;
    }
    public boolean isCompactArrays() {
        return compactArrays;
    }
    public void setCompactArrays(boolean compactArrays) {
        this.compactArrays = compactArrays;
    }   
}
