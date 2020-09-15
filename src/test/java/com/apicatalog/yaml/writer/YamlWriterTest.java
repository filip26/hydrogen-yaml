package com.apicatalog.yaml.writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

class YamlWriterTest {


    @ParameterizedTest(name = "{0}")
    @MethodSource("testCaseMethodSource")
    void testStreamWriter(TestDescription testCase) throws IOException {
        
        assertNotNull(testCase);
        assertNotNull(testCase.getInput());

        StringWriter output = new StringWriter();

        try (final InputStream is = YamlWriterTest.class.getResourceAsStream(testCase.getInput())) {

            assertNotNull(is);

            final JsonParser testParser = Json.createParser(is);
            
            assertTrue(testParser.hasNext());
            
            assertEquals(JsonParser.Event.START_OBJECT, testParser.next());

            YamlStreamWriter yamlWriter = new YamlStreamWriterImpl(new PrintWriter(output));
            
            writeObject(yamlWriter, testParser.getObject());
        }
        

        if (testCase.getExpected() == null) {
            return;
        }
        
        try (final InputStream is = YamlWriterTest.class.getResourceAsStream(testCase.getExpected())) {

            assertNotNull(is);
            
            String expected = new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
            
            assertEquals(expected, output.toString());
        }        
    }
    
    static final void writeObject(YamlStreamWriter writer, JsonObject object) {

        if (object.containsKey("@type")) {

            final String type = object.getString("@type");
            
            if ("PlainScalar".equals(type)) {
                for (JsonValue item : object.getJsonArray("@value")) {
                    writer.writePlainScalar(((JsonString)item).getString());                    
                }
                return;
            }

            if ("LiteralScalar".equals(type)) {
                writer.writeLiteralScalar(ChompingStyle.CLIP);
                for (JsonValue item : object.getJsonArray("@value")) {
                    writer.writePlainScalar(((JsonString)item).getString());                    
                }
                return;
            }

            
            fail("Unknown @type=" + type);
            return;
        }
        
        for (Map.Entry<String, JsonValue> entry : object.entrySet()) {
            
        }
        
    }
    
    
    static final void writeValue(YamlStreamWriter writer, JsonValue value) {
        
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
