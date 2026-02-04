package src.Student.controller;

import src.Student.model.Student;
import src.common.model.Submission;
import java.io.*;
import java.util.UUID;

public class StudentController {
    private final String SUBMISSION_FILE = "submissions.txt";

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
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}