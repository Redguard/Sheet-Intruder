package ch.redguard.burp.sheet_intruder.ui;

import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import ch.redguard.burp.sheet_intruder.parser.Mode;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SheetIntruderAction extends AbstractAction {
    private final Mode mode;
    private final ContextMenuEvent event;

    public SheetIntruderAction(String text, Mode mode, ContextMenuEvent event) {
        super(text);
        this.mode = mode;
        this.event = event;
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {

    }


}
