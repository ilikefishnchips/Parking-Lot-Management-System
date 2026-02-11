package main.java.view;

import javax.swing.*;

public class MainGUI {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Parking Lot Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            JTabbedPane tabs = new JTabbedPane();

            tabs.addTab("Entry (Member 2)", new EntryPanel());
            tabs.addTab("Admin (Member 4)", new AdminPanel());

            frame.add(tabs);

            frame.setVisible(true);
        });
    }
}
