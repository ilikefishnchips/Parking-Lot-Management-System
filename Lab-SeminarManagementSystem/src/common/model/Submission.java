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
    this.boardId = null; // Initialize as null
}
public String getBoardId() { return boardId; }
public void setBoardId(String boardId) { this.boardId = boardId; }
    // Getters
    // In the Submission class, add this method with the other getters:
    public String getPresentationFilePath() {
        return presentationFilePath;
    }
    public String getStudentId() { return studentId; }
    public String getTitle() { return title; }
    public String getPresentationType() { return presentationType; }
    public String getAbstractText() { return abstractText; }
    public String getSupervisor() { return supervisor; }

public String toFileString() {
    return submissionId + "|" + studentId + "|" + title + "|" + 
           abstractText + "|" + supervisor + "|" + presentationType + "|" + 
           presentationFilePath + "|" + (boardId != null ? boardId : "NONE");
}
}