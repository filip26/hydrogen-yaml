package com.apicatalog.yaml.writer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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

class YamlWriterTest {


    @ParameterizedTest(name = "{0}")
    @MethodSource("testCaseMethodSource")
    void testStreamWriter(TestDescription testCase) throws IOException {
        
        
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

    
}
