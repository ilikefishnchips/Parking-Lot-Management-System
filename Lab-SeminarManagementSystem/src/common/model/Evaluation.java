package src.common.model;

public class Evaluation {
    private String evaluationId;
    private String submissionId;
    private String evaluatorId;
    private int problemClarity;  // 1-5
    private int methodology;     // 1-5
    private int results;         // 1-5
    private int presentation;    // 1-5
    private String comments;
    private double totalScore;
    
    public Evaluation(String evaluationId, String submissionId, String evaluatorId,
                     int problemClarity, int methodology, int results, 
                     int presentation, String comments) {
        this.evaluationId = evaluationId;
        this.submissionId = submissionId;
        this.evaluatorId = evaluatorId;
        this.problemClarity = problemClarity;
        this.methodology = methodology;
        this.results = results;
        this.presentation = presentation;
        this.comments = comments;
        this.totalScore = (problemClarity + methodology + results + presentation) / 4.0;
    }
    
    // Getters
    public String getEvaluationId() { return evaluationId; }
    public String getSubmissionId() { return submissionId; }
    public String getEvaluatorId() { return evaluatorId; }
    public int getProblemClarity() { return problemClarity; }
    public int getMethodology() { return methodology; }
    public int getResults() { return results; }
    public int getPresentation() { return presentation; }
    public String getComments() { return comments; }
    public double getTotalScore() { return totalScore; }
    
    public String toFileString() {
        return evaluationId + "|" + submissionId + "|" + evaluatorId + "|" + 
               problemClarity + "|" + methodology + "|" + results + "|" + 
               presentation + "|" + comments;
    }
}