package ch.redguard.burp.sheet_intruder.parser;

import burp.api.montoya.logging.Logging;
import ch.redguard.burp.sheet_intruder.TestLogging;
import ch.redguard.burp.sheet_intruder.TestUtil;
import ch.redguard.burp.sheet_intruder.excel.ExcelParser;
import ch.redguard.burp.sheet_intruder.mock.TestByteUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReplacerTest {

    @Test
    public void testReplaceAllStrings() {
        String start = "some request data, headers";
        String tagStr = """
                <$SheetIntruder>
                {
                    "valueToReplace": "replacement",
                    "valueToReplace2": "replacement2"
                }
                </$SheetIntruder>
                """;
        String end = "Some end data";

        byte[] body = (start + tagStr + end).getBytes();

        var replacements = Map.of(
                "valueToReplace", "replacement",
                "valueToReplace2", "replacement2"
        );
        File file = TestUtil.getXlsxFile();
        Logging logging = new TestLogging();
        TagByteParser tagByteParser = new TagByteParser(body, new TestByteUtils(), new TestLogging());

        var tag = tagByteParser.getTagContent();
        Replacer replacer = new Replacer(body, replacements, tag, file, logging);

        var excelBytes = new ExcelParser(file, logging).readAndReplace(replacements, tag.getMode()).get();

        byte[] fullReplacedBody = replacer.getReplacedBody().get();

        assertTrue(new String(fullReplacedBody).startsWith(start));
        assertTrue(new String(fullReplacedBody).endsWith(end));

        assertEquals(
                start.getBytes().length + excelBytes.length + end.getBytes().length,
                fullReplacedBody.length - 1
        );
    }

    @Test
    public void testReplaceCellMode() {
        String start = "some request data, headers";
        String tagStr = """
                <$SheetIntruderCell>
                {
                    "A1": "replacement",
                    "B1": "replacement2"
                }
                </$SheetIntruderCell>
                """;
        String end = "Some end data";

        byte[] body = (start + tagStr + end).getBytes();

        var replacements = Map.of(
                "A1", "replacement",
                "B1", "replacement2"
        );
        File file = TestUtil.getXlsxFile();
        Logging logging = new TestLogging();
        TagByteParser tagByteParser = new TagByteParser(body, new TestByteUtils(), new TestLogging());

        var tag = tagByteParser.getTagContent();

        Replacer replacer = new Replacer(body, replacements, tag, file, logging);

        var excelBytes = new ExcelParser(file, logging).readAndReplace(replacements, tag.getMode()).get();

        byte[] fullReplacedBody = replacer.getReplacedBody().get();

        assertTrue(new String(fullReplacedBody).startsWith(start));
        assertTrue(new String(fullReplacedBody).endsWith(end));

        assertEquals(
                start.getBytes().length + excelBytes.length + end.getBytes().length,
                fullReplacedBody.length - 1
        );
    }
}