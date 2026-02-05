package src.Evaluator.controller;

import java.io.*;
import java.util.*;
import src.common.model.Evaluation;
import src.common.model.Submission;

public class EvaluationController {
    private final String EVALUATION_FILE = "evaluations.txt";
    private final String SUBMISSION_FILE = "submissions.txt";
    private final String ASSIGNMENT_FILE = "assignments.txt";
    private final String SESSION_FILE = "sessions.txt";
    
    // Save evaluation
    public boolean saveEvaluation(Evaluation evaluation) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(EVALUATION_FILE, true))) {
            writer.write(evaluation.toFileString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get submissions assigned to an evaluator (FIXED METHOD)
    public List<Submission> getAssignedSubmissionsForEvaluator(String evaluatorId) {
        List<Submission> assignedSubmissions = new ArrayList<>();
        
        // Step 1: Get students assigned to this evaluator from assignments.txt
        List<String> assignedStudentIds = getStudentsAssignedToEvaluator(evaluatorId);
        
        if (assignedStudentIds.isEmpty()) {
            System.out.println("No students assigned to evaluator: " + evaluatorId);
            return assignedSubmissions;
        }
        
        System.out.println("Evaluator " + evaluatorId + " assigned to students: " + assignedStudentIds);
        
        // Step 2: Load all submissions
        File submissionFile = new File(SUBMISSION_FILE);
        if (!submissionFile.exists()) {
            System.out.println("No submission file found.");
            return assignedSubmissions;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(submissionFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 7) { // Minimum 7 parts for a submission
                    String studentId = parts[1]; // parts[1] is student ID
                    
                    // Check if this student is assigned to our evaluator
                    if (assignedStudentIds.contains(studentId)) {
                        Submission sub = new Submission(
                            parts[0], // submissionId
                            parts[1], // studentId
                            parts[2], // title
                            parts[3], // abstract
                            parts[4], // supervisor
                            parts[5], // type
                            parts[6]  // file path
                        );
                        
                        // Check for board ID if it exists
                        if (parts.length > 7 && !parts[7].equals("NONE")) {
                            sub.setBoardId(parts[7]);
                        }
                        
                        assignedSubmissions.add(sub);
                        System.out.println("Found submission for student " + studentId + ": " + parts[2]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("Total submissions found for evaluator " + evaluatorId + ": " + assignedSubmissions.size());
        return assignedSubmissions;
    }
    
    // Helper method to get all student IDs assigned to a specific evaluator
    private List<String> getStudentsAssignedToEvaluator(String evaluatorId) {
        List<String> studentIds = new ArrayList<>();
        File assignmentFile = new File(ASSIGNMENT_FILE);
        
        if (!assignmentFile.exists()) {
            System.out.println("No assignment file found.");
            return studentIds;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(assignmentFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) { // assignmentId|sessionId|studentId|evaluatorId
                    String assignmentEvaluatorId = parts[3];
                    String studentId = parts[2];
                    
                    if (assignmentEvaluatorId.equals(evaluatorId)) {
                        studentIds.add(studentId);
                        System.out.println("Found assignment: " + studentId + " -> " + evaluatorId);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return studentIds;
    }
    
    // Get sessions where evaluator is assigned (for conflict checking)
    public List<String> getEvaluatorSessions(String evaluatorId) {
        List<String> sessions = new ArrayList<>();
        File sessionFile = new File(SESSION_FILE);
        
        if (!sessionFile.exists()) {
            return sessions;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(sessionFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 8) { // Has evaluators list at parts[7]
                    String sessionId = parts[0];
                    String evaluatorsStr = parts[7];
                    
                    if (!evaluatorsStr.isEmpty()) {
                        String[] evaluatorArray = evaluatorsStr.split(",");
                        for (String eval : evaluatorArray) {
                            if (eval.equals(evaluatorId)) {
                                sessions.add(sessionId);
                                break;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return sessions;
    }
    
    // Check if already evaluated
    public boolean hasEvaluated(String submissionId, String evaluatorId) {
        File file = new File(EVALUATION_FILE);
        if (!file.exists()) {
            System.out.println("No evaluation file found.");
            return false;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 8 && 
                    parts[1].equals(submissionId) && 
                    parts[2].equals(evaluatorId)) {
                    System.out.println("Already evaluated: " + submissionId + " by " + evaluatorId);
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Get evaluations for a submission
    public List<Evaluation> getEvaluationsForSubmission(String submissionId) {
        List<Evaluation> evaluations = new ArrayList<>();
        File file = new File(EVALUATION_FILE);
        if (!file.exists()) return evaluations;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 8 && parts[1].equals(submissionId)) {
                    Evaluation eval = new Evaluation(
                        parts[0], parts[1], parts[2],
                        Integer.parseInt(parts[3]),
                        Integer.parseInt(parts[4]),
                        Integer.parseInt(parts[5]),
                        Integer.parseInt(parts[6]),
                        parts[7]
                    );
                    evaluations.add(eval);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return evaluations;
    }
    
    // Calculate average score
    public double calculateAverageScore(String submissionId) {
        List<Evaluation> evaluations = getEvaluationsForSubmission(submissionId);
        if (evaluations.isEmpty()) return 0.0;
        
        double total = 0;
        for (Evaluation eval : evaluations) {
            total += eval.getTotalScore();
        }
        return total / evaluations.size();
    }
    
    // Get all evaluations by an evaluator
    public List<Evaluation> getEvaluationsByEvaluator(String evaluatorId) {
        List<Evaluation> evaluations = new ArrayList<>();
        File file = new File(EVALUATION_FILE);
        if (!file.exists()) return evaluations;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 8 && parts[2].equals(evaluatorId)) {
                    Evaluation eval = new Evaluation(
                        parts[0], parts[1], parts[2],
                        Integer.parseInt(parts[3]),
                        Integer.parseInt(parts[4]),
                        Integer.parseInt(parts[5]),
                        Integer.parseInt(parts[6]),
                        parts[7]
                    );
                    evaluations.add(eval);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return evaluations;
    }
    
    // Check if evaluator has any assignments
    public boolean hasAssignments(String evaluatorId) {
        List<String> studentIds = getStudentsAssignedToEvaluator(evaluatorId);
        return !studentIds.isEmpty();
    }
}