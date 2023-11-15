package ch.redguard.burp.sheet_intruder.parser;

import burp.api.montoya.logging.Logging;
import burp.api.montoya.utilities.ByteUtils;
import ch.redguard.burp.sheet_intruder.tag.TagType;

import java.util.Arrays;

public class TagByteParser {
    private final byte[] body;
    private final ByteUtils byteUtils;
    private final Logging logging;

    public TagByteParser(byte[] body, ByteUtils byteUtils, Logging logging) {
        this.body = body;
        this.byteUtils = byteUtils;
        this.logging = logging;
    }

    public ParsedTag getTagContent() {
        if (hasTag(body)) {
            logging.raiseDebugEvent("Found tag content, will process");
            return getBytesBetweenTags(body);
        }
        return new ParsedTag();
    }

    private ParsedTag getBytesBetweenTags(byte[] inputArray) {
        Mode mode = Mode.DEFAULT;
        String startTag, endTag;
        int startIndex, endIndex;
        startTag = TagType.VALUE_TAG.getStartTag();
        endTag = TagType.VALUE_TAG.getEndTag();
        startIndex = byteUtils.indexOf(inputArray, byteUtils.convertFromString(startTag), false);
        endIndex = byteUtils.indexOf(inputArray, byteUtils.convertFromString(endTag), false);
        if (startIndex == -1 || endIndex == -1) {
            startTag = TagType.CELL_TAG.getStartTag();
            endTag = TagType.CELL_TAG.getEndTag();
            startIndex = byteUtils.indexOf(inputArray, byteUtils.convertFromString(startTag), false);
            endIndex = byteUtils.indexOf(inputArray, byteUtils.convertFromString(endTag), false);
            if (startIndex == -1 || endIndex == -1) {
                return new ParsedTag(
                        new byte[0],
                        mode,
                        startIndex,
                        endIndex + endTag.length()
                );
            }
            mode = Mode.CELL;
        }
        return new ParsedTag(
                Arrays.copyOfRange(inputArray, startIndex + startTag.length(), endIndex),
                mode,
                startIndex,
                endIndex + endTag.length()
        );
    }


    private boolean hasTag(byte[] body) {
        //IMPROVE: handle multiple tags
        return (byteUtils.indexOf(
                body,
                byteUtils.convertFromString(TagType.VALUE_TAG.getStartTag()),
                false
        ) > -1) || (byteUtils.indexOf(
                body,
                byteUtils.convertFromString(TagType.CELL_TAG.getStartTag()),
                false
        ) > -1);
    }

}
