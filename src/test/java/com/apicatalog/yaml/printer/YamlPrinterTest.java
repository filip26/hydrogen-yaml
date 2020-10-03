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
            
            YamlPrinter yamlPrinter = new DefaultYamlPrinter(new IndentedkPrinter(output));
            
            testParser.next();
            
            write(testCase, yamlPrinter, testParser.getValue());
            
        } catch (YamlPrinterException | IOException e) {    
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
    
    static final void write(TestDescription test, YamlPrinter printer, JsonValue value) throws YamlPrinterException { 
        
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
    
    static final void writeScalar(final YamlPrinter printer, final String scalar) throws YamlPrinterException {
        
        if (scalar.startsWith("\"")) {
            printer.beginDoubleQuotedScalar();
            printLines(printer, scalar.substring(1));
            printer.endScalar();
            
        } else if (scalar.startsWith("\'")) {
            printer.beginSingleQuotedScalar();
            printLines(printer, scalar.substring(1));
            printer.endScalar();
            
        } else {
            printer.beginPlainScalar();
            printLines(printer, scalar);
            printer.endScalar();
        }
    }
    
    static final void printLines(final YamlPrinter printer, final String scalar) throws YamlPrinterException {
        boolean next = false;
        
        for (String line : scalar.split("\\r?\\n")) {
            if (next) {
                printer.println();
            }
            if (line.length() > 0) {
                printer.print(line.toCharArray());
            }
            next = true;
        }
    }
    
    static final void writeObject(TestDescription test, YamlPrinter writer, JsonObject object) throws YamlPrinterException {

        if (object.containsKey("@type")) {

            final String type = object.getString("@type");
            
            if ("LiteralScalar".equals(type)) {
                writer.beginBlockScalar(BlockScalarType.LITERAL, ChompingStyle.CLIP);
                for (JsonValue item : object.getJsonArray("@value")) {
                    
                    final String value = ((JsonString)item).getString(); 
                    
                    if (!value.isBlank()) {
                        writer.print(value.toCharArray());   
                    }
                    writer.println();
                }
                writer.endScalar();
                return;
            }

            if ("FoldedScalar".equals(type)) {
                writer.beginBlockScalar(BlockScalarType.FOLDED, ChompingStyle.CLIP);
                for (JsonValue item : object.getJsonArray("@value")) {
                    final String value = ((JsonString)item).getString(); 
                    
                    if (!value.isBlank()) {
                        writer.print(value.toCharArray());   
                    }
                    writer.println();

                }
                writer.endScalar();
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
    
    static final void writeArray(TestDescription test, YamlPrinter printer, JsonArray array) throws YamlPrinterException {
        
        printer.beginBlockSequence(test.isCompactArrays());        
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
