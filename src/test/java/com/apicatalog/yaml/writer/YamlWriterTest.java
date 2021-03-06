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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.apicatalog.yaml.TestDescription;
import com.apicatalog.yaml.Yaml;
import com.apicatalog.yaml.YamlException;
import com.apicatalog.yaml.node.YamlNode;
import com.apicatalog.yaml.node.builder.YamlMappingBuilder;
import com.apicatalog.yaml.node.builder.YamlSequenceBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonParser;

class YamlWriterTest {


    @Test
    void testAutocloseStream() {

        final TestOutputStream output = new TestOutputStream();
        
        try (final YamlWriter writer = Yaml.createWriterBuilder(output).build()) {
            assertFalse(output.isClosed());
            
        } catch (IOException e) {
            fail(e);
        }
                
        assertTrue(output.isClosed());
    }

    @Test
    void testAutocloseWriter() {

        final TestWriter output = new TestWriter();
        
        try (final YamlWriter writer = Yaml.createWriterBuilder(output).build()) {
            assertFalse(output.isClosed());
            
        } catch (IOException e) {
            fail(e);
        }
                
        assertTrue(output.isClosed());
    }
    @ParameterizedTest(name = "{0}")
    @MethodSource("testCaseMethodSource")
    void testSuite(TestDescription testCase) {
        
        assertNotNull(testCase);
        assertNotNull(testCase.getInput());

        StringWriter output = new StringWriter();

        try (final InputStream is = YamlWriterTest.class.getResourceAsStream(testCase.getInput())) {

            assertNotNull(is);

            final JsonParser testParser = Json.createParser(is);
            
            assertTrue(testParser.hasNext());

            testParser.next();

            final YamlPrintStyle style = new YamlPrintStyle();
            style.setMaxLineWidth(testCase.getMaxLineLength());

            try (YamlWriter yamlWriter = Yaml.createWriter(output, style)) {

                yamlWriter.write(toYaml(testCase, testParser.getValue()));

            }
            
        } catch (YamlException | IOException e) {    
            e.printStackTrace();
            fail(e);
        }
        if (testCase.getExpected() == null) {
            return;
        }
        
        try (final InputStream is = YamlWriterTest.class.getResourceAsStream(testCase.getExpected())) {

            assertNotNull(is);
            
            String expected = new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
            
            assertEquals(expected, output.toString());
            
        } catch (IOException e) {    
            fail(e);
        }
        
    }
    
    static final YamlNode toYaml(final TestDescription test, final JsonValue value) { 
        
        switch (value.getValueType()) {
        case OBJECT:
            final YamlMappingBuilder mb = Yaml.createMappingBuilder();
            
            value.asJsonObject().entrySet().forEach(e -> mb.add(e.getKey(), toYaml(test, e.getValue())));
                            
            return mb.build();
            
        case ARRAY:
            final YamlSequenceBuilder sb = Yaml.createSequenceBuilder();
            
            value.asJsonArray().forEach(i -> sb.add(toYaml(test, i)));
            
            return sb.build();
           
        case STRING:
            return Yaml.createScalar(((JsonString)value).getString());
            
        case TRUE:
        case FALSE:
        case NUMBER:
            return Yaml.createScalar(value.toString());
            
        case NULL:
            return YamlNode.NULL;
            
        default:
            fail("Unhandled type: " + value.getValueType());
        }
        return null;
    }
        
    static final Stream<TestDescription> testCaseMethodSource() throws IOException {
        
        try (final InputStream is = YamlWriterTest.class.getResourceAsStream("manifest.json")) {
            
            assertNotNull(is);
            
            final JsonParser jsonParser = Json.createParser(is);
            
            jsonParser.next();
            
            JsonArray tests = jsonParser.getObject().getJsonArray("sequence");
            
            return tests.stream().map(JsonObject.class::cast).map(TestDescription::of);
        }
    }
    
    static final class TestOutputStream extends ByteArrayOutputStream {
        
        private boolean closed = false;

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        };
        
        public boolean isClosed() {
            return closed;
        }
    }
    
    static final class TestWriter extends StringWriter {
        
        private boolean closed = false;

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        };
        
        public boolean isClosed() {
            return closed;
        }        
    }
}
