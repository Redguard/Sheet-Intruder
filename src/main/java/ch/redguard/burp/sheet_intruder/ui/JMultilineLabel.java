package ch.redguard.burp.sheet_intruder.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.io.Serial;

public class JMultilineLabel extends JTextArea {
    @Serial
    private static final long serialVersionUID = 1L;

    public JMultilineLabel(String text) {
        super(text);
        setEditable(false);
        setCursor(null);
        setOpaque(false);
        setFocusable(false);
        setFont(UIManager.getFont("Label.font"));
        setWrapStyleWord(true);
        setLineWrap(true);
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setAlignmentY(JLabel.CENTER_ALIGNMENT);
    }
}