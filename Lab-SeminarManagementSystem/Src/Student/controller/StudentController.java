package src.Student.controller;

import java.io.*;
import java.util.UUID;
import src.Student.model.Student;
import src.common.model.Submission;

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