package src.Coordinator.controller;

import src.common.model.Report;
import src.common.model.Award;
import src.common.model.Submission;
import src.common.model.Evaluation;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.UUID;

public class ReportController {
    private final String SUBMISSION_FILE = "submissions.txt";
    private final String EVALUATION_FILE = "evaluations.txt";
    private final String SESSION_FILE = "sessions.txt";
    private final String AWARD_FILE = "awards.txt";
    private final String REPORT_FOLDER = "reports/";
    
    // Generate seminar schedule report
    public Report generateScheduleReport() {
        String reportId = UUID.randomUUID().toString().substring(0, 8);
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Report report = new Report(reportId, "Seminar Schedule", date);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(SESSION_FILE))) {
            String line;
            report.addContentLine("SEMINAR SCHEDULE");
            report.addContentLine("================\n");
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    report.addContentLine("Session ID: " + parts[0]);
                    report.addContentLine("Date: " + parts[1] + " Time: " + parts[2]);
                    report.addContentLine("Venue: " + parts[3]);
                    report.addContentLine("Type: " + parts[4]);
                    if (parts.length > 5) {
                        report.addContentLine("Capacity: " + parts[5]);
                    }
                    report.addContentLine("---");
                }
            }
        } catch (IOException e) {
            report.addContentLine("Error: Could not read sessions file.");
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
        
        report.addContentLine("EVALUATION SUMMARY");
        report.addContentLine("==================\n");
        
        for (Map.Entry<String, Submission> entry : submissions.entrySet()) {
            String studentId = entry.getKey();
            Submission sub = entry.getValue();
            List<Evaluation> evals = evaluations.getOrDefault(studentId, new ArrayList<>());
            
            report.addContentLine("Submission: " + sub.getTitle());
            report.addContentLine("Student ID: " + studentId);
            report.addContentLine("Type: " + sub.getPresentationType());
            
            if (!evals.isEmpty()) {
                double totalScore = 0;
                for (Evaluation eval : evals) {
                    totalScore += eval.getTotalScore();
                }
                double avgScore = totalScore / evals.size();
                report.addContentLine("Average Score: " + String.format("%.2f", avgScore));
                report.addContentLine("Number of Evaluations: " + evals.size());
            } else {
                report.addContentLine("Status: Not yet evaluated");
            }
            report.addContentLine("---");
        }
        
        saveReportToFile(report);
        return report;
    }
    
    // Compute awards
    public List<Award> computeAwards() {
        List<Award> awards = new ArrayList<>();
        
        // Load submissions and evaluations
        Map<String, Submission> submissions = loadSubmissions();
        Map<String, List<Evaluation>> evaluations = loadEvaluations();
        
        // Separate oral and poster presentations
        List<Submission> oralSubmissions = new ArrayList<>();
        List<Submission> posterSubmissions = new ArrayList<>();
        
        for (Submission sub : submissions.values()) {
            if (sub.getPresentationType().contains("Oral")) {
                oralSubmissions.add(sub);
            } else {
                posterSubmissions.add(sub);
            }
        }
        
        // Compute Best Oral (highest average score)
        if (!oralSubmissions.isEmpty()) {
            Submission bestOral = findBestSubmission(oralSubmissions, evaluations);
            if (bestOral != null) {
                double score = calculateAverageScore(bestOral.getStudentId(), evaluations);
                Award award = new Award(
                    UUID.randomUUID().toString().substring(0, 8),
                    "Best Oral Presentation",
                    bestOral.getStudentId(),
                    "Student " + bestOral.getStudentId(),
                    bestOral.getTitle(),
                    score
                );
                awards.add(award);
                saveAward(award);
            }
        }
        
        // Compute Best Poster (highest average score)
        if (!posterSubmissions.isEmpty()) {
            Submission bestPoster = findBestSubmission(posterSubmissions, evaluations);
            if (bestPoster != null) {
                double score = calculateAverageScore(bestPoster.getStudentId(), evaluations);
                Award award = new Award(
                    UUID.randomUUID().toString().substring(0, 8),
                    "Best Poster Presentation",
                    bestPoster.getStudentId(),
                    "Student " + bestPoster.getStudentId(),
                    bestPoster.getTitle(),
                    score
                );
                awards.add(award);
                saveAward(award);
            }
        }
        
        // People's Choice (could be based on additional votes)
        // For now, we'll select randomly
        if (!submissions.isEmpty()) {
            Random rand = new Random();
            List<Submission> allSubs = new ArrayList<>(submissions.values());
            Submission peoplesChoice = allSubs.get(rand.nextInt(allSubs.size()));
            
            Award award = new Award(
                UUID.randomUUID().toString().substring(0, 8),
                "People's Choice Award",
                peoplesChoice.getStudentId(),
                "Student " + peoplesChoice.getStudentId(),
                peoplesChoice.getTitle(),
                0 // Not score-based
            );
            awards.add(award);
            saveAward(award);
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
            writer.write("Report Type:," + report.getReportType());
            writer.newLine();
            writer.write("Generated Date:," + report.getGeneratedDate());
            writer.newLine();
            writer.write("Report ID:," + report.getReportId());
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
    
    // Helper methods
    private Map<String, Submission> loadSubmissions() {
        Map<String, Submission> submissions = new HashMap<>();
        File file = new File(SUBMISSION_FILE);
        if (!file.exists()) return submissions;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 7) {
                    Submission sub = new Submission(parts[0], parts[1], parts[2], 
                                                  parts[3], parts[4], parts[5], parts[6]);
                    submissions.put(parts[1], sub); // studentId as key
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return submissions;
    }
    
    private Map<String, List<Evaluation>> loadEvaluations() {
        Map<String, List<Evaluation>> evaluations = new HashMap<>();
        File file = new File(EVALUATION_FILE);
        if (!file.exists()) return evaluations;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 8) {
                    Evaluation eval = new Evaluation(
                        parts[0], parts[1], parts[2],
                        Integer.parseInt(parts[3]),
                        Integer.parseInt(parts[4]),
                        Integer.parseInt(parts[5]),
                        Integer.parseInt(parts[6]),
                        parts[7]
                    );
                    
                    String studentId = parts[1]; // submissionId = studentId
                    evaluations.putIfAbsent(studentId, new ArrayList<>());
                    evaluations.get(studentId).add(eval);
                }
            }
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
    
    private void saveReportToFile(Report report) {
        File folder = new File(REPORT_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(REPORT_FOLDER + report.getReportId() + "_" + 
                report.getReportType().replace(" ", "_") + ".txt"))) {
            writer.write(report.getFormattedContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void saveAward(Award award) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(AWARD_FILE, true))) {
            writer.write(award.toFileString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}