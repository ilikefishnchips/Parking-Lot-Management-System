package model;

public class Submission {
    private String submissionId; // unique ID
    private String studentId;
    private String title;
    private String abstractText;
    private String supervisor;
    private String presentationType; // "Oral" or "Poster"
    private String presentationFilePath;

    public Submission(String submissionId, String studentId, String title, String abstractText, String supervisor, String type, String path) {
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

    // Format for saving to text file: ID|StudentID|Title|Abstract|Supervisor|Type|Path
    public String toFileString() {
        return submissionId + "|" + studentId + "|" + title + "|" + abstractText + "|" + supervisor + "|" + presentationType + "|" + presentationFilePath;
    }
}