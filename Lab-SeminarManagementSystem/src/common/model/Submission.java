package src.common.model;

public class Submission {
    private String submissionId;
    private String studentId;
    private String title;
    private String abstractText;
    private String supervisor;
    private String presentationType;
    private String presentationFilePath;
    private String boardId;

    public Submission(String submissionId, String studentId, String title, 
                     String abstractText, String supervisor, String type, String path) {
        this.submissionId = submissionId;
        this.studentId = studentId;
        this.title = title;
        this.abstractText = abstractText;
        this.supervisor = supervisor;
        this.presentationType = type;
        this.presentationFilePath = path;
        this.boardId = null;
    }
    
    // Getters
    public String getSubmissionId() { return submissionId; }
    public String getStudentId() { return studentId; }
    public String getTitle() { return title; }
    public String getAbstractText() { return abstractText; }
    public String getSupervisor() { return supervisor; }
    public String getPresentationType() { return presentationType; }
    public String getPresentationFilePath() { return presentationFilePath; }
    public String getBoardId() { return boardId; }
    
    // Setters
    public void setBoardId(String boardId) { this.boardId = boardId; }

    public String toFileString() {
        return submissionId + "|" + studentId + "|" + title + "|" + 
               abstractText + "|" + supervisor + "|" + presentationType + "|" + 
               presentationFilePath + "|" + (boardId != null ? boardId : "NONE");
    }
}