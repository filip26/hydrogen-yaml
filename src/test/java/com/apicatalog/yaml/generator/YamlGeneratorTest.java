package com.apicatalog.yaml.generator;

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

class YamlGeneratorTest {


    @ParameterizedTest(name = "{0}")
    @MethodSource("testCaseMethodSource")
    void testStreamWriter(TestDescription testCase) {
        
        assertNotNull(testCase);
        assertNotNull(testCase.getInput());

        StringWriter output = new StringWriter();

        try (final InputStream is = YamlGeneratorTest.class.getResourceAsStream(testCase.getInput())) {

            assertNotNull(is);

            final JsonParser testParser = Json.createParser(is);
            
            assertTrue(testParser.hasNext());
            
            YamlGenerator yamlWriter = new YamlGeneratorImpl(new BlockWriter(output));
            
            testParser.next();
            
            write(yamlWriter, testParser.getValue());
            
        } catch (YamlGenerationException | IOException e) {    
            fail(e);
        }

        if (testCase.getExpected() == null) {
            return;
        }
        
        try (final InputStream is = YamlGeneratorTest.class.getResourceAsStream(testCase.getExpected())) {

            assertNotNull(is);
            
            String expected = new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
            
            assertEquals(expected, output.toString());
            
        } catch (IOException e) {    
            fail(e);
        }
        
    }
    
    static final void write(YamlGenerator writer, JsonValue value) throws YamlGenerationException { 
        
        switch (value.getValueType()) {
        case OBJECT:
            writeObject(writer, value.asJsonObject());
            return;
            
        case ARRAY:
            writeArray(writer, value.asJsonArray());
            return;
           
        case STRING:
            writer.writeFlowScalar(FlowScalarType.PLAIN, ((JsonString)value).getString());
        default:
            
        }        
    }
    
    static final void writeObject(YamlGenerator writer, JsonObject object) throws YamlGenerationException {

        if (object.containsKey("@type")) {

            final String type = object.getString("@type");
            
            if ("PlainScalar".equals(type)) {
                writer.writeFlowScalar(FlowScalarType.PLAIN, object.getString("@value"));    
                return;
            }

            if ("LiteralScalar".equals(type)) {
                writer.beginBlockScalar(BlockScalarType.LITERAL, ChompingStyle.CLIP);
                for (JsonValue item : object.getJsonArray("@value")) {
                    writer.writeBlockScalar(((JsonString)item).getString());                    
                }
                writer.endBlockScalar();
                return;
            }

            if ("FoldedScalar".equals(type)) {
                writer.beginBlockScalar(BlockScalarType.FOLDED, ChompingStyle.CLIP);
                for (JsonValue item : object.getJsonArray("@value")) {
                    writer.writeBlockScalar(((JsonString)item).getString());                    
                }
                writer.endBlockScalar();
                return;
            }
            
            fail("Unknown @type=" + type);
            return;
        }
        
        writer.beginMapping();
        for (Map.Entry<String, JsonValue> entry : object.entrySet()) {
            
        }
        writer.endMapping();
    }
    
    static final void writeArray(YamlGenerator writer, JsonArray array) throws YamlGenerationException {
        
        writer.beginSequence();        
        for (JsonValue value : array) {

            write(writer, value);

        }
        writer.endSequence();        
    }
    
    static final Stream<TestDescription> testCaseMethodSource() throws IOException {
        
        try (final InputStream is = YamlGeneratorTest.class.getResourceAsStream("manifest.json")) {
            
            assertNotNull(is);
            
            final JsonParser jsonParser = Json.createParser(is);
            
            jsonParser.next();
            
            JsonArray tests = jsonParser.getObject().getJsonArray("sequence");
            
            return tests.stream().map(JsonObject.class::cast).map(TestDescription::of);
        }
    }

    
}
