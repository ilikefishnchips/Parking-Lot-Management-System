package src.Coordinator.controller;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JOptionPane;
import src.common.model.Award;
import src.common.model.Evaluation;
import src.common.model.Report;
import src.common.model.Submission;

public class ReportController {
    private final String SUBMISSION_FILE = "submissions.txt";
    private final String EVALUATION_FILE = "evaluations.txt";
    private final String SESSION_FILE = "sessions.txt";
    private final String STUDENT_FILE = "students_data.txt";
    private final String AWARD_FILE = "awards.txt";
    private final String REPORT_FOLDER = "reports/";
    
    // Generate seminar schedule report
    public Report generateScheduleReport() {
        String reportId = UUID.randomUUID().toString().substring(0, 8);
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Report report = new Report(reportId, "Seminar Schedule", date);
        
        File sessionFile = new File(SESSION_FILE);
        if (!sessionFile.exists()) {
            report.addContentLine("No sessions found.");
            return report;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(sessionFile))) {
            String line;
            report.addContentLine("SEMINAR SCHEDULE");
            report.addContentLine("================\n");
            report.addContentLine(String.format("%-12s %-20s %-20s %-15s %-20s %-10s", 
                "Session ID", "Start Date/Time", "End Date/Time", "Venue", "Type", "Capacity"));
            report.addContentLine(String.format("%-12s %-20s %-20s %-15s %-20s %-10s", 
                "----------", "-------------------", "-----------------", "-----", "----", "--------"));
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 6) {
                    String sessionId = parts[0];
                    String startDateTime = parts[1];
                    String endDateTime = parts[2];
                    String venue = parts[3];
                    String type = parts[4];
                    String capacity = parts[5];
                    
                    String studentCount = "0";
                    String evaluatorCount = "0";
                    
                    if (parts.length >= 7 && !parts[6].isEmpty()) {
                        studentCount = String.valueOf(parts[6].split(",").length);
                    }
                    
                    if (parts.length >= 8 && !parts[7].isEmpty()) {
                        evaluatorCount = String.valueOf(parts[7].split(",").length);
                    }
                    
                    report.addContentLine(String.format("%-12s %-20s %-20s %-15s %-20s %-10s", 
                        sessionId, startDateTime, endDateTime, venue, type, 
                        studentCount + "/" + capacity + " students"));
                }
            }
        } catch (IOException e) {
            report.addContentLine("Error reading sessions file: " + e.getMessage());
        }
        
        // Save report to file
        saveReportToFile(report);
        return report;
    }
    
    // Generate evaluation report
    public Report generateEvaluationReport() {
        String reportId = UUID.randomUUID().toString().substring(0, 8);
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Report report = new Report(reportId, "Evaluation Summary", date);
        
        // Load submissions and evaluations
        Map<String, Submission> submissions = loadSubmissions();
        Map<String, List<Evaluation>> evaluations = loadEvaluations();
        
        if (submissions.isEmpty()) {
            report.addContentLine("No submissions found.");
            return report;
        }
        
        report.addContentLine("EVALUATION SUMMARY REPORT");
        report.addContentLine("=========================\n");
        report.addContentLine("Generated: " + date + "\n");
        
        // Summary statistics
        int totalSubmissions = submissions.size();
        int evaluatedCount = 0;
        int notEvaluatedCount = 0;
        double totalOverallScore = 0;
        
        for (Submission sub : submissions.values()) {
            List<Evaluation> evals = evaluations.get(sub.getStudentId());
            if (evals != null && !evals.isEmpty()) {
                evaluatedCount++;
                totalOverallScore += calculateAverageScore(sub.getStudentId(), evaluations);
            } else {
                notEvaluatedCount++;
            }
        }
        
        double avgOverallScore = evaluatedCount > 0 ? totalOverallScore / evaluatedCount : 0;
        
        report.addContentLine("SUMMARY STATISTICS:");
        report.addContentLine("Total Submissions: " + totalSubmissions);
        report.addContentLine("Evaluated: " + evaluatedCount);
        report.addContentLine("Not Yet Evaluated: " + notEvaluatedCount);
        report.addContentLine("Overall Average Score: " + String.format("%.2f", avgOverallScore) + "/5.0\n");
        
        // Detailed evaluation data
        report.addContentLine("DETAILED EVALUATIONS:");
        report.addContentLine("==================================================================================================================");
        report.addContentLine(String.format("%-12s %-30s %-15s %-12s %-8s %-8s %-8s %-8s %-8s %-15s", 
            "Student ID", "Title", "Type", "Supervisor", "Prob", "Meth", "Res", "Pres", "Avg", "Evaluations"));
        report.addContentLine(String.format("%-12s %-30s %-15s %-12s %-8s %-8s %-8s %-8s %-8s %-15s", 
            "----------", "------------------------------", "---------------", "-----------", "---", "---", "---", "---", "---", "-------------"));
        
        for (Submission sub : submissions.values()) {
            List<Evaluation> evals = evaluations.get(sub.getStudentId());
            
            if (evals != null && !evals.isEmpty()) {
                double avgScore = calculateAverageScore(sub.getStudentId(), evaluations);
                double avgProblem = calculateCriteriaAverage(evals, "problem");
                double avgMethod = calculateCriteriaAverage(evals, "method");
                double avgResults = calculateCriteriaAverage(evals, "results");
                double avgPresentation = calculateCriteriaAverage(evals, "presentation");
                
                report.addContentLine(String.format("%-12s %-30s %-15s %-12s %-8.1f %-8.1f %-8.1f %-8.1f %-8.2f %-15d", 
                    sub.getStudentId(),
                    truncate(sub.getTitle(), 28),
                    truncate(sub.getPresentationType(), 13),
                    truncate(sub.getSupervisor(), 10),
                    avgProblem, avgMethod, avgResults, avgPresentation,
                    avgScore,
                    evals.size()));
            } else {
                report.addContentLine(String.format("%-12s %-30s %-15s %-12s %-8s %-8s %-8s %-8s %-8s %-15s", 
                    sub.getStudentId(),
                    truncate(sub.getTitle(), 28),
                    truncate(sub.getPresentationType(), 13),
                    truncate(sub.getSupervisor(), 10),
                    "-", "-", "-", "-", "-", "Not evaluated"));
            }
        }
        
        // Criteria breakdown
        report.addContentLine("\n\nCRITERIA BREAKDOWN:");
        report.addContentLine("==================\n");
        
        Map<String, Double> criteriaAverages = calculateAllCriteriaAverages(evaluations);
        report.addContentLine("Problem Clarity: " + String.format("%.2f", criteriaAverages.get("problem")) + "/5.0");
        report.addContentLine("Methodology: " + String.format("%.2f", criteriaAverages.get("method")) + "/5.0");
        report.addContentLine("Results: " + String.format("%.2f", criteriaAverages.get("results")) + "/5.0");
        report.addContentLine("Presentation: " + String.format("%.2f", criteriaAverages.get("presentation")) + "/5.0");
        
        saveReportToFile(report);
        return report;
    }
    
    // Compute awards
    public List<Award> computeAwards() {
        List<Award> awards = new ArrayList<>();
        
        // Load submissions and evaluations
        Map<String, Submission> submissions = loadSubmissions();
        Map<String, List<Evaluation>> evaluations = loadEvaluations();
        
        if (submissions.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No submissions found to compute awards.");
            return awards;
        }
        
        // Separate oral and poster presentations
        List<Submission> oralSubmissions = new ArrayList<>();
        List<Submission> posterSubmissions = new ArrayList<>();
        
        for (Submission sub : submissions.values()) {
            if (sub.getPresentationType().toLowerCase().contains("oral")) {
                oralSubmissions.add(sub);
            } else if (sub.getPresentationType().toLowerCase().contains("poster")) {
                posterSubmissions.add(sub);
            }
        }
        
        // Compute Best Oral Presentation
        if (!oralSubmissions.isEmpty()) {
            Submission bestOral = findBestSubmission(oralSubmissions, evaluations);
            if (bestOral != null) {
                double score = calculateAverageScore(bestOral.getStudentId(), evaluations);
                String studentName = getStudentName(bestOral.getStudentId());
                
                Award award = new Award(
                    UUID.randomUUID().toString().substring(0, 8),
                    "Best Oral Presentation",
                    bestOral.getStudentId(),
                    studentName,
                    bestOral.getTitle(),
                    score
                );
                awards.add(award);
                saveAward(award);
                System.out.println("Best Oral: " + studentName + " - Score: " + score);
            }
        }
        
        // Compute Best Poster Presentation
        if (!posterSubmissions.isEmpty()) {
            Submission bestPoster = findBestSubmission(posterSubmissions, evaluations);
            if (bestPoster != null) {
                double score = calculateAverageScore(bestPoster.getStudentId(), evaluations);
                String studentName = getStudentName(bestPoster.getStudentId());
                
                Award award = new Award(
                    UUID.randomUUID().toString().substring(0, 8),
                    "Best Poster Presentation",
                    bestPoster.getStudentId(),
                    studentName,
                    bestPoster.getTitle(),
                    score
                );
                awards.add(award);
                saveAward(award);
                System.out.println("Best Poster: " + studentName + " - Score: " + score);
            }
        }
        
        // People's Choice Award (based on highest number of evaluations or random if none)
        if (!submissions.isEmpty()) {
            Submission peoplesChoice = findPeoplesChoice(submissions.values(), evaluations);
            if (peoplesChoice != null) {
                double score = calculateAverageScore(peoplesChoice.getStudentId(), evaluations);
                String studentName = getStudentName(peoplesChoice.getStudentId());
                
                Award award = new Award(
                    UUID.randomUUID().toString().substring(0, 8),
                    "People's Choice Award",
                    peoplesChoice.getStudentId(),
                    studentName,
                    peoplesChoice.getTitle(),
                    score
                );
                awards.add(award);
                saveAward(award);
                System.out.println("People's Choice: " + studentName);
            }
        }
        
        // Most Innovative Research Award (highest methodology score)
        if (!submissions.isEmpty()) {
            Submission mostInnovative = findMostInnovative(submissions.values(), evaluations);
            if (mostInnovative != null) {
                double score = calculateAverageScore(mostInnovative.getStudentId(), evaluations);
                String studentName = getStudentName(mostInnovative.getStudentId());
                
                Award award = new Award(
                    UUID.randomUUID().toString().substring(0, 8),
                    "Most Innovative Research",
                    mostInnovative.getStudentId(),
                    studentName,
                    mostInnovative.getTitle(),
                    score
                );
                awards.add(award);
                saveAward(award);
                System.out.println("Most Innovative: " + studentName);
            }
        }
        
        return awards;
    }
    
    // Export report to CSV
    public boolean exportReportToCSV(Report report, String filename) {
        File folder = new File(REPORT_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REPORT_FOLDER + filename))) {
            writer.write("Report Type," + report.getReportType());
            writer.newLine();
            writer.write("Generated Date," + report.getGeneratedDate());
            writer.newLine();
            writer.write("Report ID," + report.getReportId());
            writer.newLine();
            writer.newLine();
            
            for (String line : report.getContentLines()) {
                writer.write(line.replaceAll(",", ";"));
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ============ HELPER METHODS ============
    
    private Map<String, Submission> loadSubmissions() {
        Map<String, Submission> submissions = new HashMap<>();
        File file = new File(SUBMISSION_FILE);
        
        if (!file.exists()) {
            System.out.println("Submission file not found: " + SUBMISSION_FILE);
            return submissions;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 7) {
                    Submission sub = new Submission(
                        parts[0],  // submissionId
                        parts[1],  // studentId
                        parts[2],  // title
                        parts[3],  // abstract
                        parts[4],  // supervisor
                        parts[5],  // presentationType
                        parts[6]   // filePath
                    );
                    
                    // Check for board assignment
                    if (parts.length > 7 && !parts[7].equals("NONE")) {
                        sub.setBoardId(parts[7]);
                    }
                    
                    submissions.put(parts[1], sub); // studentId as key
                }
            }
            System.out.println("Loaded " + submissions.size() + " submissions");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return submissions;
    }
    
    private Map<String, List<Evaluation>> loadEvaluations() {
        Map<String, List<Evaluation>> evaluations = new HashMap<>();
        File file = new File(EVALUATION_FILE);
        
        if (!file.exists()) {
            System.out.println("Evaluation file not found: " + EVALUATION_FILE);
            return evaluations;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 8) {
                    Evaluation eval = new Evaluation(
                        parts[0],  // evaluationId
                        parts[1],  // submissionId (which is studentId)
                        parts[2],  // evaluatorId
                        Integer.parseInt(parts[3]),  // problemClarity
                        Integer.parseInt(parts[4]),  // methodology
                        Integer.parseInt(parts[5]),  // results
                        Integer.parseInt(parts[6]),  // presentation
                        parts[7]   // comments
                    );
                    
                    String studentId = parts[1]; // submissionId = studentId
                    evaluations.putIfAbsent(studentId, new ArrayList<>());
                    evaluations.get(studentId).add(eval);
                }
            }
            System.out.println("Loaded evaluations for " + evaluations.size() + " students");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return evaluations;
    }
    
    private Submission findBestSubmission(List<Submission> submissions, 
                                         Map<String, List<Evaluation>> evaluations) {
        if (submissions.isEmpty()) return null;
        
        Submission best = null;
        double highestScore = -1;
        
        for (Submission sub : submissions) {
            double score = calculateAverageScore(sub.getStudentId(), evaluations);
            if (score > highestScore) {
                highestScore = score;
                best = sub;
            }
        }
        
        return best;
    }
    
    private double calculateAverageScore(String studentId, 
                                       Map<String, List<Evaluation>> evaluations) {
        List<Evaluation> evals = evaluations.getOrDefault(studentId, new ArrayList<>());
        if (evals.isEmpty()) return 0.0;
        
        double total = 0;
        for (Evaluation eval : evals) {
            total += eval.getTotalScore();
        }
        return total / evals.size();
    }
    
    private double calculateCriteriaAverage(List<Evaluation> evaluations, String criteria) {
        if (evaluations.isEmpty()) return 0.0;
        
        double total = 0;
        for (Evaluation eval : evaluations) {
            switch (criteria.toLowerCase()) {
                case "problem":
                    total += eval.getProblemClarity();
                    break;
                case "method":
                    total += eval.getMethodology();
                    break;
                case "results":
                    total += eval.getResults();
                    break;
                case "presentation":
                    total += eval.getPresentation();
                    break;
            }
        }
        return total / evaluations.size();
    }
    
    private Map<String, Double> calculateAllCriteriaAverages(Map<String, List<Evaluation>> allEvaluations) {
        Map<String, Double> averages = new HashMap<>();
        averages.put("problem", 0.0);
        averages.put("method", 0.0);
        averages.put("results", 0.0);
        averages.put("presentation", 0.0);
        
        int totalEvaluations = 0;
        double totalProblem = 0, totalMethod = 0, totalResults = 0, totalPresentation = 0;
        
        for (List<Evaluation> evals : allEvaluations.values()) {
            for (Evaluation eval : evals) {
                totalProblem += eval.getProblemClarity();
                totalMethod += eval.getMethodology();
                totalResults += eval.getResults();
                totalPresentation += eval.getPresentation();
                totalEvaluations++;
            }
        }
        
        if (totalEvaluations > 0) {
            averages.put("problem", totalProblem / totalEvaluations);
            averages.put("method", totalMethod / totalEvaluations);
            averages.put("results", totalResults / totalEvaluations);
            averages.put("presentation", totalPresentation / totalEvaluations);
        }
        
        return averages;
    }
    
    private Submission findPeoplesChoice(Collection<Submission> submissions, 
                                        Map<String, List<Evaluation>> evaluations) {
        if (submissions.isEmpty()) return null;
        
        // Find submission with most evaluations
        Submission mostEvaluated = null;
        int maxEvaluations = -1;
        
        for (Submission sub : submissions) {
            List<Evaluation> evals = evaluations.getOrDefault(sub.getStudentId(), new ArrayList<>());
            if (evals.size() > maxEvaluations) {
                maxEvaluations = evals.size();
                mostEvaluated = sub;
            }
        }
        
        return mostEvaluated;
    }
    
    private Submission findMostInnovative(Collection<Submission> submissions, 
                                         Map<String, List<Evaluation>> evaluations) {
        if (submissions.isEmpty()) return null;
        
        Submission mostInnovative = null;
        double highestMethodologyScore = -1;
        
        for (Submission sub : submissions) {
            List<Evaluation> evals = evaluations.getOrDefault(sub.getStudentId(), new ArrayList<>());
            if (!evals.isEmpty()) {
                double methodScore = calculateCriteriaAverage(evals, "method");
                if (methodScore > highestMethodologyScore) {
                    highestMethodologyScore = methodScore;
                    mostInnovative = sub;
                }
            }
        }
        
        return mostInnovative;
    }
    
    private String getStudentName(String studentId) {
        File file = new File(STUDENT_FILE);
        if (!file.exists()) {
            return "Student " + studentId;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 4 && parts[0].equals(studentId)) {
                    return parts[3]; // Name is at index 3
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return "Student " + studentId;
    }
    
    private String truncate(String text, int maxLength) {
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
    
    private void saveReportToFile(Report report) {
        File folder = new File(REPORT_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        
        String filename = REPORT_FOLDER + report.getReportId() + "_" + 
                         report.getReportType().replace(" ", "_") + ".txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(report.getFormattedContent());
            System.out.println("Report saved to: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void saveAward(Award award) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(AWARD_FILE, true))) {
            writer.write(award.toFileString());
            writer.newLine();
            System.out.println("Award saved: " + award.getAwardType() + " - " + award.getStudentName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}