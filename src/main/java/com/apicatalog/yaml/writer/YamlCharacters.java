package com.apicatalog.yaml.writer;

import java.util.function.Predicate;

public final class YamlCharacters {

    public static final Predicate<Character> IS_PRINTABLE = ch ->
                                                                ch == 0x9 || ch == 0xA || ch == 0xD
                                                                || (ch >= 0x20 && ch <= 0x7E)
                                                                || ch == 0x85
                                                                || (ch >= 0xA0 && ch <= 0xD7FF)
                                                                || (ch >= 0xE000 && ch <= 0xFFFD);

    public static final Predicate<Character> IS_CONTROL = ch -> 
                                                                ch == '{' || ch == '}' || ch == '['  || ch == ']'
                                                                || ch == ',' || ch == '&' || ch == '*' || ch == '#' 
                                                                || ch == '?' || ch == '|' || ch == '-' || ch == '<' 
                                                                || ch == '>' || ch == '=' || ch == '!' || ch == '%' 
                                                                || ch == '@' || ch == '`';

    private YamlCharacters() {}
}
