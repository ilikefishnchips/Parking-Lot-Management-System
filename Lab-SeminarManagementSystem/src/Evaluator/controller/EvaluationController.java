package src.Evaluator.controller;

import src.common.model.Evaluation;
import src.common.model.Submission;
import java.io.*;
import java.util.*;

public class EvaluationController {
    private final String EVALUATION_FILE = "evaluations.txt";
    private final String SUBMISSION_FILE = "submissions.txt";
    private final String ASSIGNMENT_FILE = "assignments.txt";
    
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
    
    // Get submissions assigned to an evaluator
    public List<Submission> getAssignedSubmissions(String evaluatorId) {
        List<Submission> assignedSubmissions = new ArrayList<>();
        Map<String, String> assignments = loadAssignments();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(SUBMISSION_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 7) {
                    String studentId = parts[1];
                    // Check if this evaluator is assigned to this student
                    if (assignments.containsKey(studentId) && 
                        assignments.get(studentId).equals(evaluatorId)) {
                        Submission sub = new Submission(parts[0], parts[1], parts[2], 
                                                      parts[3], parts[4], parts[5], parts[6]);
                        assignedSubmissions.add(sub);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return assignedSubmissions;
    }
    
    // Check if already evaluated
    public boolean hasEvaluated(String submissionId, String evaluatorId) {
        File file = new File(EVALUATION_FILE);
        if (!file.exists()) return false;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 8 && 
                    parts[1].equals(submissionId) && 
                    parts[2].equals(evaluatorId)) {
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
    
    // Load assignments from file
    private Map<String, String> loadAssignments() {
        Map<String, String> assignments = new HashMap<>();
        File file = new File(ASSIGNMENT_FILE);
        if (!file.exists()) return assignments;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    assignments.put(parts[2], parts[3]); // studentId -> evaluatorId
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return assignments;
    }
}