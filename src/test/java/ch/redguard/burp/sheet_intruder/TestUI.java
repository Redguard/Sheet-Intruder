package ch.redguard.burp.sheet_intruder;

import ch.redguard.burp.sheet_intruder.ui.MainPanel;

import javax.swing.*;
import java.awt.*;

public class TestUI {

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("Burp Suite - Sheet Intruder");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLayout(new BorderLayout());

        jFrame.setPreferredSize(new Dimension(1500, 800));
        JMenuBar menuBar = new JMenuBar();
        jFrame.setJMenuBar(menuBar);
        jFrame.pack();
        var uiPanel = new MainPanel();

        Container content = jFrame.getContentPane();
        content.setLayout(new BorderLayout());

        content.add(uiPanel, BorderLayout.WEST);
        jFrame.setVisible(true);
    }
}
