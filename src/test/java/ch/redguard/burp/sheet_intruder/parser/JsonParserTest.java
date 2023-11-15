package ch.redguard.burp.sheet_intruder.parser;

import ch.redguard.burp.sheet_intruder.TestLogging;
import ch.redguard.burp.sheet_intruder.mock.TestByteUtils;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonParserTest {

    @Test
    void parseJson() {
        TestLogging logging = new TestLogging();
        var tagParser = new TagByteParser("""
                test<$SheetIntruder>
                {
                    "valueToReplace": "replacement",
                    "valueToReplace2": "replacement2"
                }
                </$SheetIntruder>
                """.getBytes(),
                new TestByteUtils(),
                logging
        );
        var hashMap = new JsonParser(tagParser.getTagContent().getContent(), logging).parseJson();

        var expectedHashMap = Map.of(
                "valueToReplace", "replacement",
                "valueToReplace2", "replacement2"
        );

        assertEquals(expectedHashMap, hashMap);
    }

    @Test
    void parseJsonCellMode() {
        TestLogging logging = new TestLogging();
        var tagParser = new TagByteParser("""
                test<$SheetIntruderCell>
                {
                    "A1": "replacement",
                    "B1": "replacement2"
                }
                </$SheetIntruderCell>
                """.getBytes(),
                new TestByteUtils(),
                logging
        );
        var hashMap = new JsonParser(tagParser.getTagContent().getContent(), logging).parseJson();

        var expectedHashMap = Map.of(
                "A1", "replacement",
                "B1", "replacement2"
        );

        assertEquals(expectedHashMap, hashMap);
    }

    @Test
    void parseInvalidJson() {
        TestLogging logging = new TestLogging();
        var tagParser = new TagByteParser("""
                <$SheetIntruderCell>
                {
                    "A1": "replacement":
                    "B1": "replacement2"
                                
                </$SheetIntruderCell>
                """.getBytes(),
                new TestByteUtils(),
                logging
        );
        var hashMap = new JsonParser(tagParser.getTagContent().getContent(), logging).parseJson();

        var expectedHashMap = Map.of();

        assertEquals(expectedHashMap, hashMap);
    }
}