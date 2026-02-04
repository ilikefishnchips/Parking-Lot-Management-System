package src.Student.controller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import src.Student.model.Student;
import src.common.model.Submission;

public class StudentController {
    private final String SUBMISSION_FILE = "submissions.txt";
// Add this method to get poster submissions
public List<Submission> getPosterSubmissions() {
    List<Submission> posters = new ArrayList<>();
    File file = new File(SUBMISSION_FILE);
    if (!file.exists()) return posters;
    
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\|");
            if (parts.length >= 7) {
                String type = parts[5];
                if (type.toLowerCase().contains("poster")) {
                    Submission sub = new Submission(parts[0], parts[1], parts[2], 
                                                   parts[3], parts[4], parts[5], parts[6]);
                    if (parts.length > 7 && !parts[7].equals("NONE")) {
                        sub.setBoardId(parts[7]);
                    }
                    posters.add(sub);
                }
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return posters;
}
    public boolean submitResearch(Student student, String title, String abstractText, 
                                  String supervisor, String type, String filePath) {
        String submissionId = UUID.randomUUID().toString().substring(0, 8);
        Submission newSubmission = new Submission(
            submissionId, 
            student.getStudentId(), 
            title, 
            abstractText, 
            supervisor, 
            type, 
            filePath
        );

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SUBMISSION_FILE, true))) {
            writer.write(newSubmission.toFileString());
            writer.newLine();
            try (BufferedWriter assignWriter = new BufferedWriter(new FileWriter("assignments.txt", true))) {
                String assignmentId = UUID.randomUUID().toString().substring(0, 8);
                // Hard-code evaluator "EVAL001" to grade this student
                String assignmentLine = assignmentId + "|SESS001|" + student.getStudentId() + "|EVAL001";
                assignWriter.write(assignmentLine);
                assignWriter.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}