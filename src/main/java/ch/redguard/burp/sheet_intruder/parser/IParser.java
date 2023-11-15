package ch.redguard.burp.sheet_intruder.parser;

import java.util.Map;
import java.util.Optional;

public interface IParser {
    Optional<byte[]> readAndReplace(Map<String, String> replacements, Mode mode);
}
