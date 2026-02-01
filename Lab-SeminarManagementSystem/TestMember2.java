// No package declaration here because it is in the root folder

import model.Student;
import UI.StudentPanel;
import javax.swing.JFrame;

public class TestMember2 {
    public static void main(String[] args) {
        // Create dummy student
        Student dummy = new Student("112233", "john.doe", "pass123", "John Doe");
        
        JFrame frame = new JFrame("Member 2 Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        
        // Load Member 2's Panel
        frame.add(new StudentPanel(dummy));
        
        frame.setVisible(true);
    }
}