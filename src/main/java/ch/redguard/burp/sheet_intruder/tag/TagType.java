package ch.redguard.burp.sheet_intruder.tag;

import ch.redguard.burp.sheet_intruder.parser.Mode;

public enum TagType {
    VALUE_TAG("<$SheetIntruder>", "</$SheetIntruder>", Mode.DEFAULT),
    CELL_TAG("<$SheetIntruderCell>", "</$SheetIntruderCell>", Mode.CELL);

    private final String startTag;
    private final String endTag;
    private final Mode mode;

    TagType(String startTag, String endTag, Mode mode) {
        this.startTag = startTag;
        this.endTag = endTag;
        this.mode = mode;
    }

    public String getStartTag() {
        return startTag;
    }

    public String getEndTag() {
        return endTag;
    }

    public Mode getMode() {
        return mode;
    }
}
