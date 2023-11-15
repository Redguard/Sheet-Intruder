package ch.redguard.burp.sheet_intruder;

import burp.api.montoya.extension.ExtensionUnloadingHandler;
import ch.redguard.burp.sheet_intruder.ui.SelectedFile;

public class SheetIntruderUnloadingHandler implements ExtensionUnloadingHandler {
    @Override
    public void extensionUnloaded() {
        SelectedFile.getInstance().unloadFile();
    }
}
