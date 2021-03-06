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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.apicatalog.yaml.TestDescription;
import com.apicatalog.yaml.Yaml;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.stream.JsonParser;

class YamlParserTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("testCaseMethodSource")
    void testSuite(TestDescription testCase) throws IOException {
        
        assumeFalse(true);      // skip tests
        
        assertNotNull(testCase);
        assertNotNull(testCase.getInput());
        
        try (final InputStream is = YamlParserTest.class.getResourceAsStream(testCase.getInput())) {

            assertNotNull(is);
            
            final YamlParser parser = Yaml.createParser(is);
            
            assertNotNull(parser);

            while (parser.hasNext()) {
                
//                Event event = parser.next();
                
            }
            
//            document = (new JsonDocumentParser()).parse(URI.create("http://example.com"), is);
//            
//            assertTrue(testCase.isPositiveTest());
            
//        } catch (DocumentParserException e) {
//            
//            if (testCase.isNegativeTest()) {
//                return;
//            }
//            
//            fail(e.getMessage(), e);
        } catch (YamlParsingException e) {
            fail(e.getMessage());
        }
        
//        assertNotNull(document);
        
        //TODO compare
    }
    
    static final Stream<TestDescription> testCaseMethodSource() throws IOException {
        
        try (final InputStream is = YamlParserTest.class.getResourceAsStream("manifest.json")) {
            
            assertNotNull(is);
            
            final JsonParser jsonParser = Json.createParser(is);
            
            jsonParser.next();
            
            JsonArray tests = jsonParser.getObject().getJsonArray("sequence");
            
            return tests.stream().map(JsonObject.class::cast).map(TestDescription::of);
        }
    }
    
}
