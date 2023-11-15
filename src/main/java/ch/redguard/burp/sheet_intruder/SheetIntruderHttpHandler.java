package ch.redguard.burp.sheet_intruder;

import burp.api.montoya.core.ByteArray;
import burp.api.montoya.http.handler.HttpRequestToBeSent;
import burp.api.montoya.http.handler.HttpResponseReceived;
import burp.api.montoya.http.handler.RequestToBeSentAction;
import burp.api.montoya.http.handler.ResponseReceivedAction;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.utilities.ByteUtils;
import ch.redguard.burp.sheet_intruder.parser.JsonParser;
import ch.redguard.burp.sheet_intruder.parser.ParsedTag;
import ch.redguard.burp.sheet_intruder.parser.Replacer;
import ch.redguard.burp.sheet_intruder.parser.TagByteParser;
import ch.redguard.burp.sheet_intruder.ui.SelectedFile;

import java.io.File;
import java.util.Optional;


public class SheetIntruderHttpHandler implements burp.api.montoya.http.handler.HttpHandler {

    private final ByteUtils byteUtils;
    private final Logging logging;

    SheetIntruderHttpHandler(ByteUtils byteUtils, Logging logging) {
        this.byteUtils = byteUtils;
        this.logging = logging;
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        HttpRequest newRequest = requestToBeSent;
        switch (requestToBeSent.toolSource().toolType()) {
            case SCANNER, INTRUDER, REPEATER, PROXY -> {
                var body = requestToBeSent.body().getBytes();
                var tagByteParser = new TagByteParser(body, byteUtils, logging);
                ParsedTag parsedTag = tagByteParser.getTagContent();

                if (parsedTag.isEmptyOrInvalid()) {
                    logging.raiseDebugEvent("No valid tag found, not modifying request");
                    break;
                }

                var replacements = new JsonParser(parsedTag.getContent(), logging).parseJson();
                if (replacements.isEmpty()) {
                    logging.raiseDebugEvent("No valid json found, not modifying request");
                    break;
                }
                logging.raiseDebugEvent("Found replacement config " + replacements);

                Optional<File> selectedFile = SelectedFile.getInstance().getFile();

                if (selectedFile.isEmpty()) {
                    logging.raiseDebugEvent("No file configured, not modifying request");
                    break;
                }

                logging.raiseDebugEvent("Using configured file " + selectedFile.get().getPath());

                var newBody =
                        new Replacer(body, replacements, parsedTag, selectedFile.get(), logging).getReplacedBody();
                if (newBody.isPresent()) {
                    newRequest = requestToBeSent.withBody(ByteArray.byteArray(newBody.get()));
                }
            }
            default -> {
            }
        }
        return RequestToBeSentAction.continueWith(newRequest);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        return ResponseReceivedAction.continueWith(responseReceived);
    }
}
