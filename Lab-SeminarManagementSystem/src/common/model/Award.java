package src.common.model;

public class Award {
    private String awardId;
    private String awardType;  // "Best Oral", "Best Poster", "People's Choice"
    private String studentId;
    private String studentName;
    private String submissionTitle;
    private double score;
    
    public Award(String awardId, String awardType, String studentId, 
                String studentName, String submissionTitle, double score) {
        this.awardId = awardId;
        this.awardType = awardType;
        this.studentId = studentId;
        this.studentName = studentName;
        this.submissionTitle = submissionTitle;
        this.score = score;
    }
    
    // Getters
    public String getAwardId() { return awardId; }
    public String getAwardType() { return awardType; }
    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getSubmissionTitle() { return submissionTitle; }
    public double getScore() { return score; }
    
    public String toFileString() {
        return awardId + "|" + awardType + "|" + studentId + "|" + 
               studentName + "|" + submissionTitle + "|" + score;
    }
}