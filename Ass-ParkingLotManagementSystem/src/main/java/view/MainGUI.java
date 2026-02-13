package main.java.view;

import javax.swing.*;

public class MainGUI {
    public static void main(String[] args) {

        // swing components are not thread‑safe so excuting it line by line to prevent bugs
        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Parking Lot Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            JTabbedPane tabs = new JTabbedPane();
            
            // Each tab panel is instantiated immediately when called
            tabs.addTab("Entry (Member 2)", new EntryPanel());
            tabs.addTab("Exit (Member 3)", new ExitPanel());
            tabs.addTab("Admin (Member 4)", new AdminPanel());

            frame.add(tabs);

            frame.setVisible(true);
        });
    }
}
