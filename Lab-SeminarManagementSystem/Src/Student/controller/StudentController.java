package controller;

import model.Student;
import model.Submission;
import java.io.*;
import java.util.UUID;

public class StudentController {
    
    private final String SUBMISSION_FILE = "submissions.txt";

    // Method 1: Handle Student Registration (Simplified)
    public boolean registerStudent(String username, String password, String name) {
        // Logic to append to users.txt would go here
        // For this module, we focus on the Submission part
        return true; 
    }

    // Method 2: Submit Research Details
    public boolean submitResearch(Student student, String title, String abstractText, String supervisor, String type, String filePath) {
        // 1. Create Submission Object
        String submissionId = UUID.randomUUID().toString().substring(0, 8); // Generate short unique ID
        Submission newSubmission = new Submission(
            submissionId, 
            student.getStudentId(), 
            title, 
            abstractText, 
            supervisor, 
            type, 
            filePath
        );

        // 2. Save to File (Simulating Database)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SUBMISSION_FILE, true))) {
            writer.write(newSubmission.toFileString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method 3: Check if student already submitted (Optional helper)
    public boolean hasSubmitted(String studentId) {
        File file = new File(SUBMISSION_FILE);
        if (!file.exists()) return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|"); // Split by pipe
                if (parts.length > 1 && parts[1].equals(studentId)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}