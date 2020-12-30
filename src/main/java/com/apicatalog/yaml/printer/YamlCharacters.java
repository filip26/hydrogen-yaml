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
package com.apicatalog.yaml.printer;

import java.util.function.Predicate;

final class YamlCharacters {

    static final Predicate<Character> IS_PRINTABLE = ch ->
                                                        ch == 0x9 || ch == 0xA || ch == 0xD
                                                        || (ch >= 0x20 && ch <= 0x7E)
                                                        || ch == 0x85
                                                        || (ch >= 0xA0 && ch <= 0xD7FF)
                                                        || (ch >= 0xE000 && ch <= 0xFFFD);

    static final Predicate<Character> IS_CONTROL = ch ->  ch == ':'
                                                            || ch == '{' || ch == '}' || ch == '['  || ch == ']'
                                                            || ch == ',' || ch == '&' || ch == '*' || ch == '#' 
                                                            || ch == '?' || ch == '|' || ch == '-' || ch == '<' 
                                                            || ch == '>' || ch == '=' || ch == '!' || ch == '%' 
                                                            || ch == '@' || ch == '`';

    static final char[][] DOUBLE_ESCAPE_MAPPING = new char[][] {
        new char[] {0x00, '0'}, new char[] {0x07, 'a'}, new char[] {0x08, 'b'}, new char[] {0x09, 't'}, 
        new char[] {0x0a, 'n'}, new char[] {0x0b, 'v'}, new char[] {0x0c, 'f'}, new char[] {0x0d, 'r'}, 
        new char[] {0x1b, 'e'}, new char[] {0xa0, '_'}, new char[] {0x85, 'N'}, new char[] {0x2028, 'L'}, 
        new char[] {0x2029, 'P'},
        new char[] { '\\', '\\'}, new char[] {'"', '"'}, new char[] {'/', '/'}
    };

    private YamlCharacters() {}
}
