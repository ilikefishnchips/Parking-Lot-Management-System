package Member2_assignment;

import javax.swing.*;

public class MainGUI {
    public static void main(String[] args) {
        // Run Swing on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Member 2 - Entry System Prototype");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 400);
            
            // Add your panel to the frame
            frame.add(new EntryPanel());
            
            frame.setVisible(true);
        });
    }
}