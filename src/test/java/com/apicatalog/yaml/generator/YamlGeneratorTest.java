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
            
            YamlGenerator yamlWriter = new YamlGeneratorImpl(new IndentedkWriter(output));
            
            testParser.next();
            
            write(testCase, yamlWriter, testParser.getValue());
            
        } catch (YamlGenerationException | IOException e) {    
            e.printStackTrace();
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
    
    static final void write(TestDescription test, YamlGenerator writer, JsonValue value) throws YamlGenerationException { 
        
        switch (value.getValueType()) {
        case OBJECT:
            writeObject(test, writer, value.asJsonObject());
            return;
            
        case ARRAY:
            writeArray(test, writer, value.asJsonArray());
            return;
           
        case STRING:
            writeScalar(writer, ((JsonString)value).getString());
            return;
            
        case TRUE:
        case FALSE:
        case NUMBER:
            writeScalar(writer, value.toString());
            return;
            
        case NULL:
            writer.skip();
            return;
            
        default:
            fail("Unhandled type: " + value.getValueType());
        }        
    }
    
    static final void writeScalar(final YamlGenerator writer, final String scalar) throws YamlGenerationException {
        
        if (scalar.startsWith("\"")) {
            writer.beginFlowScalar(FlowScalarType.DOUBLE_QUOTED);
            printLines(writer, scalar.substring(1));
            writer.endFlowScalar();
            
        } else if (scalar.startsWith("\'")) {
            writer.beginFlowScalar(FlowScalarType.SINGLE_QUOTED);
            printLines(writer, scalar.substring(1));
            writer.endFlowScalar();
            
        } else {
            writer.beginFlowScalar(FlowScalarType.PLAIN);
            printLines(writer, scalar);

            writer.endFlowScalar();
        }
    }
    
    static final void printLines(final YamlGenerator writer, final String scalar) throws YamlGenerationException {
        boolean next = false;
        
        for (String line : scalar.split("\\r?\\n")) {
            if (next) {
                writer.println();
            }

            writer.print(line);  
            next = true;
        }
    }
    
    static final void writeObject(TestDescription test, YamlGenerator writer, JsonObject object) throws YamlGenerationException {

        if (object.containsKey("@type")) {

            final String type = object.getString("@type");
            
            if ("LiteralScalar".equals(type)) {
                writer.beginBlockScalar(BlockScalarType.LITERAL, ChompingStyle.CLIP);
                for (JsonValue item : object.getJsonArray("@value")) {
                    writer.print(((JsonString)item).getString());  
                    writer.println();
                }
                writer.endBlockScalar();
                return;
            }

            if ("FoldedScalar".equals(type)) {
                writer.beginBlockScalar(BlockScalarType.FOLDED, ChompingStyle.CLIP);
                for (JsonValue item : object.getJsonArray("@value")) {
                    writer.print(((JsonString)item).getString());
                    writer.println();
                }
                writer.endBlockScalar();
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
        
        writer.enBlockdMapping();
    }
    
    static final void writeArray(TestDescription test, YamlGenerator writer, JsonArray array) throws YamlGenerationException {
        
        writer.beginBlockSequence(test.isCompactArrays());        
        for (JsonValue value : array) {

            write(test, writer, value);

        }
        writer.endBlockSequence();        
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
