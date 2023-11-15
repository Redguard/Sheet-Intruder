package ch.redguard.burp.sheet_intruder.parser;

import burp.api.montoya.logging.Logging;
import ch.redguard.burp.sheet_intruder.excel.ExcelParser;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Replacer {
    private final byte[] body;
    private final Map<String, String> replacements;
    private final File file;
    private final Logging logging;
    private final ParsedTag parsedTag;

    public Replacer(byte[] body, Map<String, String> replacements, ParsedTag parsedTag, File file, Logging logging) {
        this.body = body;
        this.replacements = replacements;
        this.parsedTag = parsedTag;
        this.file = file;
        this.logging = logging;
    }

    public static byte[] concat(byte[]... arrays) {
        int len = Arrays.stream(arrays).filter(Objects::nonNull)
                .mapToInt(s -> s.length).sum();

        byte[] result = new byte[len];
        int lengthSoFar = 0;

        for (byte[] array : arrays) {
            if (array != null) {
                System.arraycopy(array, 0, result, lengthSoFar, array.length);
                lengthSoFar += array.length;
            }
        }

        return result;
    }

    public Optional<byte[]> getReplacedBody() {
        var excelBytes = new ExcelParser(file, logging).readAndReplace(replacements, parsedTag.getMode());
        return excelBytes.map(bytes -> {
            logging.raiseDebugEvent("Replacing strings in file '" + file.getName() + "' with replacements '" + replacements + "'");
            var tagStartIndex = parsedTag.getStartIndex();
            var tagEndIndex = parsedTag.getEndIndex();
            var beforeBytes = Arrays.copyOfRange(body, 0, tagStartIndex);
            var afterBytes = Arrays.copyOfRange(body, tagEndIndex, body.length);
            return concat(beforeBytes, bytes, afterBytes);
        });
    }

}
