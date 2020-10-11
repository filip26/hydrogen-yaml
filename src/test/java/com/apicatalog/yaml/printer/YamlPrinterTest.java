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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.apicatalog.yaml.TestDescription;
import com.apicatalog.yaml.writer.YamlPrintStyle;

class YamlPrinterTest {


    @ParameterizedTest(name = "{0}")
    @MethodSource("testCaseMethodSource")
    void testSuite(TestDescription testCase) {
        
        assertNotNull(testCase);
        assertNotNull(testCase.getInput());

        StringWriter output = new StringWriter();

        try (final InputStream is = YamlPrinterTest.class.getResourceAsStream(testCase.getInput())) {

            assertNotNull(is);

            final JsonParser testParser = Json.createParser(is);
            
            assertTrue(testParser.hasNext());
            
            final YamlPrintStyle style = new YamlPrintStyle();
            
            style.setCompactArrays(testCase.isCompactArrays());
            style.setMaxLineWidth(testCase.getMaxLineLength());
            
            YamlPrinter yamlPrinter = new DefaultYamlPrinter(new IndentedPrinter(output), style);
            
            testParser.next();
            
            write(testCase, yamlPrinter, testParser.getValue());
            
        } catch (IOException e) {    
            e.printStackTrace();
            fail(e);
        }

        if (testCase.getExpected() == null) {
            return;
        }
        
        try (final InputStream is = YamlPrinterTest.class.getResourceAsStream(testCase.getExpected())) {

            assertNotNull(is);
            
            String expected = new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
            
            assertEquals(expected, output.toString());
            
        } catch (IOException e) {    
            fail(e);
        }
        
    }
    
    static final void write(TestDescription test, YamlPrinter printer, JsonValue value) throws IOException { 
        
        switch (value.getValueType()) {
        case OBJECT:
            writeObject(test, printer, value.asJsonObject());
            return;
            
        case ARRAY:
            writeArray(test, printer, value.asJsonArray());
            return;
           
        case STRING:
            writeScalar(printer, ((JsonString)value).getString());
            return;
            
        case TRUE:
        case FALSE:
        case NUMBER:
            writeScalar(printer, value.toString());
            return;
            
        case NULL:
            printer.printNull();
            return;
            
        default:
            fail("Unhandled type: " + value.getValueType());
        }        
    }
    
    static final void writeScalar(final YamlPrinter printer, final String scalar) throws IOException {
        
        final char[] chars = scalar.toCharArray();
        
        if (scalar.startsWith("\"")) {
            printer.printDoubleQuotedScalar(chars, 1, chars.length - 1);
            
        } else if (scalar.startsWith("\'")) {
            printer.printSingleQuotedScalar(chars, 1, chars.length - 1);
            
        } else {
            printer.printPlainScalar(chars, 0, chars.length);
        }
    }
        
    static final void writeObject(TestDescription test, YamlPrinter writer, JsonObject object) throws IOException {

        if (object.containsKey("@type")) {

            final String type = object.getString("@type");

            final String scalar = object.getJsonString("@value").getString();
            final char[] chars = scalar.toCharArray();

            if ("LiteralScalar".equals(type)) {           
                writer.printLiteralScalar(chars, 0, chars.length);
                return;
            }

            if ("FoldedScalar".equals(type)) {
                writer.printFoldedScalar(chars, 0, chars.length);
                return;
            }
            
            fail("Unknown @type=" + type);
            return;
        }
        
        writer.beginBlockMapping();
        
        for (Map.Entry<String, JsonValue> entry : object.entrySet()) {            
            writeScalar(writer, entry.getKey());
            write(test, writer, entry.getValue());
        }
        
        writer.endBlockdMapping();
    }
    
    static final void writeArray(TestDescription test, YamlPrinter printer, JsonArray array) throws IOException {
        
        printer.beginBlockSequence();        
        for (JsonValue value : array) {

            write(test, printer, value);

        }
        printer.endBlockSequence();        
    }
    
    static final Stream<TestDescription> testCaseMethodSource() throws IOException {
        
        try (final InputStream is = YamlPrinterTest.class.getResourceAsStream("manifest.json")) {
            
            assertNotNull(is);
            
            final JsonParser jsonParser = Json.createParser(is);
            
            jsonParser.next();
            
            JsonArray tests = jsonParser.getObject().getJsonArray("sequence");
            
            return tests.stream().map(JsonObject.class::cast).map(TestDescription::of);
        }
    }
}
