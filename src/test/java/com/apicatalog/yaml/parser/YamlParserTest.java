package com.apicatalog.yaml.parser;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.apicatalog.yaml.TestDescription;
import com.apicatalog.yaml.Yaml;
import com.apicatalog.yaml.parser.YamlParser.Event;

class YamlParserTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("testCaseMethodSource")
    void testSuite(TestDescription testCase) throws IOException {
        
        assertNotNull(testCase);
        assertNotNull(testCase.getInput());
        
        try (final InputStream is = YamlParserTest.class.getResourceAsStream(testCase.getInput())) {

            assertNotNull(is);
            
            final YamlParser parser = Yaml.createParser(is);
            
            assertNotNull(parser);

            while (parser.hasNext()) {
                
                Event event = parser.next();
                
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