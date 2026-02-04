package src.Coordinator.controller;

import src.common.model.Evaluation;
import src.common.model.Submission;
import java.io.*;
import java.util.*;
import java.text.DecimalFormat;

public class EnhancedAnalyticsController {
    private final String SUBMISSION_FILE = "submissions.txt";
    private final String EVALUATION_FILE = "evaluations.txt";
    
    // 1. Comprehensive Statistical Analysis
    public Map<String, Object> getComprehensiveStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Load data
        List<Submission> submissions = loadAllSubmissions();
        List<Evaluation> evaluations = loadAllEvaluations();
        
        if (submissions.isEmpty() || evaluations.isEmpty()) {
            stats.put("message", "Insufficient data for analysis");
            return stats;
        }
        
        // Basic counts
        stats.put("totalSubmissions", submissions.size());
        stats.put("totalEvaluations", evaluations.size());
        
        // Separate by type
        long oralCount = submissions.stream()
            .filter(s -> s.getPresentationType().toLowerCase().contains("oral"))
            .count();
        long posterCount = submissions.size() - oralCount;
        stats.put("oralSubmissions", oralCount);
        stats.put("posterSubmissions", posterCount);
        
        // Score analysis
        analyzeScores(stats, evaluations);
        
        // Time trend analysis (if dates were tracked)
        analyzeTrends(stats, evaluations);
        
        // Evaluator consistency analysis
        analyzeEvaluatorConsistency(stats, evaluations);
        
        // Supervisor performance
        analyzeSupervisorPerformance(stats, submissions, evaluations);
        
        return stats;
    }
    
    // 2. Score Analysis
    private void analyzeScores(Map<String, Object> stats, List<Evaluation> evaluations) {
        double[] problemScores = new double[evaluations.size()];
        double[] methodScores = new double[evaluations.size()];
        double[] resultsScores = new double[evaluations.size()];
        double[] presentationScores = new double[evaluations.size()];
        double[] totalScores = new double[evaluations.size()];
        
        DecimalFormat df = new DecimalFormat("#.##");
        
        for (int i = 0; i < evaluations.size(); i++) {
            Evaluation eval = evaluations.get(i);
            problemScores[i] = eval.getProblemClarity();
            methodScores[i] = eval.getMethodology();
            resultsScores[i] = eval.getResults();
            presentationScores[i] = eval.getPresentation();
            totalScores[i] = eval.getTotalScore();
        }
        
        stats.put("avgProblem", df.format(calculateAverage(problemScores)));
        stats.put("avgMethod", df.format(calculateAverage(methodScores)));
        stats.put("avgResults", df.format(calculateAverage(resultsScores)));
        stats.put("avgPresentation", df.format(calculateAverage(presentationScores)));
        stats.put("avgOverall", df.format(calculateAverage(totalScores)));
        
        stats.put("stdDevOverall", df.format(calculateStandardDeviation(totalScores)));
        stats.put("medianScore", df.format(calculateMedian(totalScores)));
        stats.put("scoreRange", getScoreRange(totalScores));
    }
    
    // 3. Trend Analysis
    private void analyzeTrends(Map<String, Object> stats, List<Evaluation> evaluations) {
        // Group by hypothetical time periods (you would need to add timestamps)
        Map<String, List<Double>> periodScores = new HashMap<>();
        
        // This is a placeholder - in reality, you'd group by actual dates
        periodScores.put("Early", new ArrayList<>());
        periodScores.put("Middle", new ArrayList<>());
        periodScores.put("Late", new ArrayList<>());
        
        // Distribute evaluations (for demo purposes)
        int third = evaluations.size() / 3;
        for (int i = 0; i < evaluations.size(); i++) {
            String period;
            if (i < third) period = "Early";
            else if (i < 2 * third) period = "Middle";
            else period = "Late";
            
            periodScores.get(period).add(evaluations.get(i).getTotalScore());
        }
        
        Map<String, Double> trendAverages = new LinkedHashMap<>();
        for (Map.Entry<String, List<Double>> entry : periodScores.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                double avg = entry.getValue().stream().mapToDouble(d -> d).average().orElse(0);
                trendAverages.put(entry.getKey(), avg);
            }
        }
        stats.put("trendAnalysis", trendAverages);
    }
    
    // 4. Evaluator Consistency Analysis
    private void analyzeEvaluatorConsistency(Map<String, Object> stats, List<Evaluation> evaluations) {
        Map<String, List<Double>> evaluatorScores = new HashMap<>();
        
        for (Evaluation eval : evaluations) {
            evaluatorScores.putIfAbsent(eval.getEvaluatorId(), new ArrayList<>());
            evaluatorScores.get(eval.getEvaluatorId()).add(eval.getTotalScore());
        }
        
        Map<String, Map<String, Object>> evaluatorStats = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : evaluatorScores.entrySet()) {
            Map<String, Object> evalStats = new HashMap<>();
            List<Double> scores = entry.getValue();
            
            if (scores.size() > 1) {
                double avg = scores.stream().mapToDouble(d -> d).average().orElse(0);
                double stdDev = calculateStandardDeviation(
                    scores.stream().mapToDouble(d -> d).toArray()
                );
                double variance = stdDev * stdDev;
                
                evalStats.put("average", avg);
                evalStats.put("stdDev", stdDev);
                evalStats.put("variance", variance);
                evalStats.put("consistency", stdDev < 1.0 ? "High" : 
                             stdDev < 2.0 ? "Medium" : "Low");
                evalStats.put("numEvaluations", scores.size());
            }
            
            evaluatorStats.put(entry.getKey(), evalStats);
        }
        
        stats.put("evaluatorAnalysis", evaluatorStats);
    }
    
    // 5. Supervisor Performance Analysis
    private void analyzeSupervisorPerformance(Map<String, Object> stats, 
                                            List<Submission> submissions, 
                                            List<Evaluation> evaluations) {
        Map<String, SupervisorStats> supervisorMap = new HashMap<>();
        
        // Map evaluations to submissions via student ID
        Map<String, Double> studentScores = new HashMap<>();
        for (Evaluation eval : evaluations) {
            studentScores.put(eval.getSubmissionId(), eval.getTotalScore());
        }
        
        // Group submissions by supervisor
        for (Submission sub : submissions) {
            String supervisor = sub.getSupervisor();
            supervisorMap.putIfAbsent(supervisor, new SupervisorStats());
            
            SupervisorStats statsObj = supervisorMap.get(supervisor);
            statsObj.incrementCount();
            
            if (studentScores.containsKey(sub.getStudentId())) {
                statsObj.addScore(studentScores.get(sub.getStudentId()));
            }
        }
        
        Map<String, Map<String, Object>> supervisorResults = new HashMap<>();
        for (Map.Entry<String, SupervisorStats> entry : supervisorMap.entrySet()) {
            Map<String, Object> supStats = new HashMap<>();
            SupervisorStats s = entry.getValue();
            
            supStats.put("numStudents", s.getCount());
            supStats.put("avgScore", s.getAverageScore());
            supStats.put("totalEvaluated", s.getEvaluatedCount());
            
            supervisorResults.put(entry.getKey(), supStats);
        }
        
        stats.put("supervisorAnalysis", supervisorResults);
    }
    
    // 6. Generate Visualizable Data
    public Map<String, Object> getChartData() {
        Map<String, Object> chartData = new HashMap<>();
        List<Evaluation> evaluations = loadAllEvaluations();
        
        if (evaluations.isEmpty()) {
            return chartData;
        }
        
        // Score distribution histogram
        Map<String, Integer> scoreDistribution = new LinkedHashMap<>();
        for (int i = 1; i <= 5; i++) {
            scoreDistribution.put(String.valueOf(i), 0);
        }
        
        for (Evaluation eval : evaluations) {
            int roundedScore = (int) Math.round(eval.getTotalScore());
            scoreDistribution.put(String.valueOf(roundedScore), 
                scoreDistribution.get(String.valueOf(roundedScore)) + 1);
        }
        chartData.put("scoreDistribution", scoreDistribution);
        
        // Criteria comparison
        Map<String, Double> criteriaAverages = new LinkedHashMap<>();
        double totalProblem = 0, totalMethod = 0, totalResults = 0, totalPresentation = 0;
        
        for (Evaluation eval : evaluations) {
            totalProblem += eval.getProblemClarity();
            totalMethod += eval.getMethodology();
            totalResults += eval.getResults();
            totalPresentation += eval.getPresentation();
        }
        
        criteriaAverages.put("Problem Clarity", totalProblem / evaluations.size());
        criteriaAverages.put("Methodology", totalMethod / evaluations.size());
        criteriaAverages.put("Results", totalResults / evaluations.size());
        criteriaAverages.put("Presentation", totalPresentation / evaluations.size());
        
        chartData.put("criteriaAverages", criteriaAverages);
        
        return chartData;
    }
    
    // 7. Generate Executive Summary
    public String generateExecutiveSummary() {
        Map<String, Object> stats = getComprehensiveStatistics();
        StringBuilder summary = new StringBuilder();
        
        summary.append("=== EXECUTIVE ANALYTICS SUMMARY ===\n");
        summary.append("Generated on: ").append(new Date()).append("\n");
        summary.append("====================================\n\n");
        
        // Overall statistics
        summary.append("OVERALL STATISTICS:\n");
        summary.append("Total Submissions: ").append(stats.get("totalSubmissions")).append("\n");
        summary.append("Oral Presentations: ").append(stats.get("oralSubmissions")).append("\n");
        summary.append("Poster Presentations: ").append(stats.get("posterSubmissions")).append("\n");
        summary.append("Total Evaluations: ").append(stats.get("totalEvaluations")).append("\n\n");
        
        // Score analysis
        summary.append("SCORE ANALYSIS:\n");
        summary.append("Overall Average Score: ").append(stats.get("avgOverall")).append("/5.0\n");
        summary.append("Score Standard Deviation: ").append(stats.get("stdDevOverall")).append("\n");
        summary.append("Median Score: ").append(stats.get("medianScore")).append("\n");
        summary.append("Score Range: ").append(stats.get("scoreRange")).append("\n\n");
        
        // Criteria breakdown
        summary.append("CRITERIA BREAKDOWN (Average Scores):\n");
        summary.append("  Problem Clarity: ").append(stats.get("avgProblem")).append("/5.0\n");
        summary.append("  Methodology: ").append(stats.get("avgMethod")).append("/5.0\n");
        summary.append("  Results: ").append(stats.get("avgResults")).append("/5.0\n");
        summary.append("  Presentation: ").append(stats.get("avgPresentation")).append("/5.0\n\n");
        
        return summary.toString();
    }
    
    // Utility methods
    private List<Submission> loadAllSubmissions() {
        List<Submission> submissions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(SUBMISSION_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 7 || parts.length == 8) {
                    Submission sub = new Submission(parts[0], parts[1], parts[2], 
                                                  parts[3], parts[4], parts[5], parts[6]);
                    if (parts.length == 8 && !parts[7].equals("NONE")) {
                        sub.setBoardId(parts[7]);
                    }
                    submissions.add(sub);
                }
            }
        } catch (IOException e) {
            // File might not exist yet
        }
        return submissions;
    }
    
    private List<Evaluation> loadAllEvaluations() {
        List<Evaluation> evaluations = new ArrayList<>();
        File file = new File(EVALUATION_FILE);
        if (!file.exists()) return evaluations;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 8) {
                    evaluations.add(new Evaluation(parts[0], parts[1], parts[2],
                        Integer.parseInt(parts[3]), Integer.parseInt(parts[4]),
                        Integer.parseInt(parts[5]), Integer.parseInt(parts[6]), parts[7]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return evaluations;
    }
    
    private double calculateAverage(double[] values) {
        if (values.length == 0) return 0;
        double sum = 0;
        for (double v : values) sum += v;
        return sum / values.length;
    }
    
    private double calculateStandardDeviation(double[] values) {
        if (values.length < 2) return 0;
        double mean = calculateAverage(values);
        double sum = 0;
        for (double v : values) {
            sum += Math.pow(v - mean, 2);
        }
        return Math.sqrt(sum / values.length);
    }
    
    private double calculateMedian(double[] values) {
        if (values.length == 0) return 0;
        Arrays.sort(values);
        int middle = values.length / 2;
        if (values.length % 2 == 1) {
            return values[middle];
        } else {
            return (values[middle - 1] + values[middle]) / 2.0;
        }
    }
    
    private String getScoreRange(double[] values) {
        if (values.length == 0) return "N/A";
        double min = Arrays.stream(values).min().orElse(0);
        double max = Arrays.stream(values).max().orElse(0);
        return String.format("%.2f - %.2f", min, max);
    }
    
    // Helper class for supervisor stats
    class SupervisorStats {
        private int count = 0;
        private double totalScore = 0;
        private int evaluatedCount = 0;
        
        public void incrementCount() { count++; }
        public void addScore(double score) { 
            totalScore += score; 
            evaluatedCount++;
        }
        public int getCount() { return count; }
        public double getAverageScore() { 
            return evaluatedCount > 0 ? totalScore / evaluatedCount : 0; 
        }
        public int getEvaluatedCount() { return evaluatedCount; }
    }
}