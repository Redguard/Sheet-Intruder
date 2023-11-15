package ch.redguard.burp.sheet_intruder;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.extension.Extension;
import burp.api.montoya.logging.Logging;
import ch.redguard.burp.sheet_intruder.ui.MainPanel;

import javax.swing.*;

public class SheetIntruder implements BurpExtension {
    @Override
    public void initialize(MontoyaApi api) {
        Extension extension = api.extension();
        extension.setName("Sheet Intruder");
        extension.registerUnloadingHandler(new SheetIntruderUnloadingHandler());
        Logging logging = api.logging();
        logging.logToOutput("Sheet Intruder loading...");

        var uiPanel = new MainPanel();

        var pane = new JTabbedPane();
        pane.addTab("Select Excel", uiPanel);

        var registration = api.userInterface().registerSuiteTab("Sheet Intruder", pane);
        api.logging().raiseDebugEvent("Registered tab: " + registration.isRegistered());

        var handlerRegistration =
                api.http().registerHttpHandler(new SheetIntruderHttpHandler(api.utilities().byteUtils(),
                        api.logging()));
        logging.logToOutput("Registering HTTP Handler: " + handlerRegistration.isRegistered());

        var contextMenuRegistration =
                api.userInterface().registerContextMenuItemsProvider(new SheetIntruderMenuItemsProvider(api.utilities().byteUtils(), api.logging()));
        logging.logToOutput("Registering Context Menu Handler: " + contextMenuRegistration.isRegistered());

        logging.logToOutput("Sheet Intruder loaded");
    }


}
