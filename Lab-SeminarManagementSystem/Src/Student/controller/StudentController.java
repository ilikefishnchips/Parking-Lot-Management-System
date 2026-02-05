package src.Student.controller;

import java.io.*;
import java.util.*;
import src.Coordinator.model.Session;
import src.Student.model.Student;
import src.common.model.Submission;

public class StudentController {
    private final String SUBMISSION_FILE = "submissions.txt";
    private final String SESSION_FILE = "sessions.txt";
    
    // Get sessions assigned to student
    public List<Session> getStudentSessions(String studentId) {
        List<Session> sessions = new ArrayList<>();
        File file = new File(SESSION_FILE);
        if (!file.exists()) return sessions;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 8) {
                    // Check if student is in this session
                    String[] studentIds = parts[6].split(",");
                    if (Arrays.asList(studentIds).contains(studentId)) {
                        Session session = new Session(parts[0], parts[1], parts[2], 
                                                     parts[3], parts[4], Integer.parseInt(parts[5]));
                        
                        // Add students
                        for (String id : studentIds) {
                            if (!id.isEmpty()) {
                                session.addStudent(id);
                            }
                        }
                        
                        // Add evaluators
                        String[] evaluatorIds = parts[7].split(",");
                        for (String evalId : evaluatorIds) {
                            if (!evalId.isEmpty()) {
                                session.addEvaluator(evalId);
                            }
                        }
                        
                        sessions.add(session);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sessions;
    }
    
    // Submit research with evaluator choice
    public boolean submitResearch(Student student, Session session, String title, 
                                  String abstractText, String supervisor, 
                                  String type, String filePath, String chosenEvaluatorId) {
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
            
            // Create assignment with chosen evaluator
            try (BufferedWriter assignWriter = new BufferedWriter(new FileWriter("assignments.txt", true))) {
                String assignmentId = UUID.randomUUID().toString().substring(0, 8);
                String assignmentLine = assignmentId + "|" + session.getSessionId() + "|" + 
                                      student.getStudentId() + "|" + chosenEvaluatorId;
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
    
    // For backward compatibility
    public boolean submitResearch(Student student, String title, String abstractText, 
                                  String supervisor, String type, String filePath) {
        // Get first session for student
        List<Session> sessions = getStudentSessions(student.getStudentId());
        if (sessions.isEmpty()) {
            return false;
        }
        
        Session firstSession = sessions.get(0);
        String evaluatorId = "EVAL001"; // Default
        
        // Try to get first available evaluator
        if (!firstSession.getEvaluatorIds().isEmpty()) {
            evaluatorId = firstSession.getEvaluatorIds().get(0);
        }
        
        return submitResearch(student, firstSession, title, abstractText, 
                             supervisor, type, filePath, evaluatorId);
    }
}