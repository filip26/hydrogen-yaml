package com.apicatalog.yaml.writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
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
import com.apicatalog.yaml.Yaml;
import com.apicatalog.yaml.YamlException;
import com.apicatalog.yaml.YamlMappingBuilder;
import com.apicatalog.yaml.YamlNode;
import com.apicatalog.yaml.YamlSequenceBuilder;
import com.apicatalog.yaml.printer.IndentedkPrinter;
import com.apicatalog.yaml.printer.YamlPrinterImpl;

class YamlWriterTest {


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
            style.setMaxLineWidth(15);
            
            final YamlPrinterImpl yamlPrinter = new YamlPrinterImpl(new IndentedkPrinter(output));
            
            try (YamlWriter yamlWriter = new DefaultYamlWriter(yamlPrinter, style)) {

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
            
            value.asJsonObject().entrySet().forEach(e -> mb.add(Yaml.createScalar(e.getKey()), toYaml(test, e.getValue())));
                            
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
}
