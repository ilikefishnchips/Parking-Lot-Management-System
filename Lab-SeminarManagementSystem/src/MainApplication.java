import Student.model.Student;
import Evaluator.model.Evaluator;
import Coordinator.model.Coordinator;
import Student.ui.StudentPanel;
import Evaluator.ui.EvaluatorPanel;
import Coordinator.ui.CoordinatorPanel;
import common.ui.LoginPanel;
import javax.swing.*;

public class MainApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create main window
            JFrame frame = new JFrame("Seminar Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null); // Center window
            
            // Start with login screen
            LoginPanel loginPanel = new LoginPanel(frame);
            frame.add(loginPanel);
            
            frame.setVisible(true);
        });
    }
}