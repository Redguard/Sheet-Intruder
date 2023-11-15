package ch.redguard.burp.sheet_intruder;

import ch.redguard.burp.sheet_intruder.mock.TestByteUtils;
import ch.redguard.burp.sheet_intruder.tag.TagType;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SheetIntruderMenuItemsProviderTest {

    @Test
    void addTagToPosition() {
        var provider = new SheetIntruderMenuItemsProvider(new TestByteUtils(), new TestLogging());

        byte[] body = "before_after".getBytes();
        int position = 6;

        Optional<byte[]> resultBody = provider.addTagToPosition(body, 0, position, TagType.CELL_TAG);
        assertEquals(
                "before" + TagType.CELL_TAG.getStartTag() + TagType.CELL_TAG.getEndTag() + "_after",
                new String(resultBody.get())
        );
    }
}