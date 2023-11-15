package ch.redguard.burp.sheet_intruder.parser;

public class ParsedTag {
    private final byte[] content;
    private final Mode mode;
    private int startIndex;
    private int endIndex;

    ParsedTag(byte[] content, Mode mode, int startIndex, int endIndex) {
        this.content = content;
        this.mode = mode;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    ParsedTag() {
        this.content = new byte[0];
        this.mode = Mode.DEFAULT;
    }

    public byte[] getContent() {
        return content;
    }

    public Mode getMode() {
        return mode;
    }

    public boolean isEmptyOrInvalid() {
        return this.content.length == 0;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }
}
