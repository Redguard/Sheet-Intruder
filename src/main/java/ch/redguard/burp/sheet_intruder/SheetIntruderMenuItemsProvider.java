package ch.redguard.burp.sheet_intruder;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import burp.api.montoya.utilities.ByteUtils;
import ch.redguard.burp.sheet_intruder.tag.TagType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SheetIntruderMenuItemsProvider implements ContextMenuItemsProvider {

    private final ByteUtils byteUtils;
    private final Logging logging;

    public SheetIntruderMenuItemsProvider(ByteUtils byteUtils, Logging logging) {
        this.byteUtils = byteUtils;
        this.logging = logging;
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        switch (event.invocationType()) {
            case INTRUDER_PAYLOAD_POSITIONS:
            case MESSAGE_EDITOR_REQUEST:
            case MESSAGE_VIEWER_RESPONSE:
                break;
            default:
                return null;
        }

        logging.raiseDebugEvent("Context is in right mode, will add tags");

        final JMenuItem valueModeTag = new JMenuItem("Value Mode Tag");
        valueModeTag.addActionListener(generateTagActionListener(event, TagType.VALUE_TAG));

        final JMenuItem cellModeTag = new JMenuItem("Cell Mode Tag");
        cellModeTag.addActionListener(generateTagActionListener(event, TagType.CELL_TAG));

        return List.of(valueModeTag, cellModeTag);
    }

    ActionListener generateTagActionListener(final ContextMenuEvent event, TagType tagType) {
        return e -> event.messageEditorRequestResponse().ifPresent(m -> {
            logging.raiseDebugEvent("Generating context menu action listener for " + tagType.getStartTag());
            logging.raiseDebugEvent("m.selectionContext() " + m.selectionContext());

            if (m.selectionContext() == MessageEditorHttpRequestResponse.SelectionContext.REQUEST) {
                HttpRequest request = m.requestResponse().request();
                var requestBytes = request.toByteArray().getBytes();
                var caretPosition = m.caretPosition() + 2;

                addTagToPosition(requestBytes, request.bodyOffset(), caretPosition, tagType).ifPresent(newBody ->
                        m.setRequest(request.withBody(ByteArray.byteArray(newBody)))
                );
            }
        });
    }

    Optional<byte[]> addTagToPosition(byte[] requestBytes, int bodyOffset, int caretPosition, TagType tagType) {
        byte[] tagStart = this.byteUtils.convertFromString(tagType.getStartTag());
        byte[] tagEnd = this.byteUtils.convertFromString(tagType.getEndTag());
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(Arrays.copyOfRange(requestBytes, 0, caretPosition));

            outputStream.write(tagStart);
            outputStream.write(tagEnd);

            outputStream.write(Arrays.copyOfRange(requestBytes, caretPosition, requestBytes.length));

            outputStream.flush();
            var fullRequest = outputStream.toByteArray();
            var onlyBody = Arrays.copyOfRange(fullRequest, bodyOffset, fullRequest.length);

            return Optional.of(onlyBody);
        } catch (RuntimeException | IOException e1) {
            logging.logToError("Could not create context action listener: " + e1.getMessage());
        }
        return Optional.empty();
    }
}

