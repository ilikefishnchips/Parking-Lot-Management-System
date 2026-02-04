package src.common.model;

public class Submission {
    private String submissionId;
    private String studentId;
    private String title;
    private String abstractText;
    private String supervisor;
    private String presentationType;
    private String presentationFilePath;

    public Submission(String submissionId, String studentId, String title, 
                     String abstractText, String supervisor, String type, String path) {
        this.submissionId = submissionId;
        this.studentId = studentId;
        this.title = title;
        this.abstractText = abstractText;
        this.supervisor = supervisor;
        this.presentationType = type;
        this.presentationFilePath = path;
    }

    // Getters
    public String getStudentId() { return studentId; }
    public String getTitle() { return title; }
    public String getPresentationType() { return presentationType; }
    public String getAbstractText() { return abstractText; }
    public String getSupervisor() { return supervisor; }

    public String toFileString() {
        return submissionId + "|" + studentId + "|" + title + "|" + 
               abstractText + "|" + supervisor + "|" + presentationType + "|" + presentationFilePath;
    }
}