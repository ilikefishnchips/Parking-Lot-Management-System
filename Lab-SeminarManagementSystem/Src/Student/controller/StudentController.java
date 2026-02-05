package src.Student.controller;

import java.io.*;
import java.util.*;
import src.Student.model.Student;
import src.common.model.Submission;

public class StudentController {
    private final String SUBMISSION_FILE = "submissions.txt";
    private final String ASSIGNMENT_FILE = "assignments.txt";
    private StudentDataController studentDataController;
    
    public StudentController() {
        this.studentDataController = new StudentDataController();
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
            // Save submission
            writer.write(newSubmission.toFileString());
            writer.newLine();
            
            // Save/update student data with preferences
            student.setPreferredPresentationType(type);
            studentDataController.saveStudentData(student);
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get student's assignment info
    public Map<String, String> getStudentAssignment(String studentId) {
        Map<String, String> assignment = new HashMap<>();
        
        // First check student data file
        Student student = studentDataController.loadStudent(studentId);
        
        if (student != null) {
            assignment.put("sessionId", student.getAssignedSessionId());
            assignment.put("evaluatorId", student.getAssignedEvaluatorId());
            assignment.put("preferredType", student.getPreferredPresentationType());
            assignment.put("status", student.getStatus());
        }
        
        // Also check assignments file directly
        File assignmentFile = new File(ASSIGNMENT_FILE);
        if (assignmentFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(assignmentFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 4 && parts[2].equals(studentId)) {
                        assignment.put("sessionId", parts[1]);
                        assignment.put("evaluatorId", parts[3]);
                        assignment.put("assignmentId", parts[0]);
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return assignment;
    }
    
    // In StudentController.java - add this method
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
    
    // Check if student has submitted
    public boolean hasStudentSubmitted(String studentId) {
        File file = new File(SUBMISSION_FILE);
        if (!file.exists()) return false;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2 && parts[1].equals(studentId)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}