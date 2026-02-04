import src.common.ui.LoginPanel;
import javax.swing.*;

public class MainApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Seminar Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            
            LoginPanel loginPanel = new LoginPanel(frame);
            frame.add(loginPanel);
            
            frame.setVisible(true);
        });
    }
}