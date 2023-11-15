package ch.redguard.burp.sheet_intruder.parser;

import ch.redguard.burp.sheet_intruder.TestLogging;
import ch.redguard.burp.sheet_intruder.mock.TestByteUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParsedTagByteParserTest {

    @Test
    void getTagContent() {
        var tagParser = new TagByteParser("test<$SheetIntruder>content</$SheetIntruder>".getBytes(),
                new TestByteUtils(), new TestLogging());
        assertEquals("content", new String(tagParser.getTagContent().getContent()));
    }

    @Test
    void getTagContentCellMode() {
        var tagParser = new TagByteParser("test<$SheetIntruderCell>content</$SheetIntruderCell>".getBytes(),
                new TestByteUtils(), new TestLogging());
        assertEquals("content", new String(tagParser.getTagContent().getContent()));
        assertEquals(Mode.CELL, tagParser.getTagContent().getMode());
    }

    @Test
    void testEmptyTag() {
        var tagParser = new TagByteParser("test<$SheetIntruderCell></$SheetIntruderCell>".getBytes(),
                new TestByteUtils(), new TestLogging());
        assertEquals("", new String(tagParser.getTagContent().getContent()));
        assertEquals(Mode.CELL, tagParser.getTagContent().getMode());
    }

    @Test
    void testInvalidTag() {
        var tagParser = new TagByteParser("test<$SheetIntruderCelltestse></$SheetIntruderCell>".getBytes(),
                new TestByteUtils(), new TestLogging());
        assertTrue(tagParser.getTagContent().isEmptyOrInvalid());
        assertEquals(Mode.DEFAULT, tagParser.getTagContent().getMode());
    }

    @Test
    void testMismatchedTag() {
        var tagParser = new TagByteParser("test<$SheetIntruder></$SheetIntruderCell>".getBytes(), new TestByteUtils()
                , new TestLogging());
        assertTrue(tagParser.getTagContent().isEmptyOrInvalid());
        assertEquals(Mode.DEFAULT, tagParser.getTagContent().getMode());
    }
}