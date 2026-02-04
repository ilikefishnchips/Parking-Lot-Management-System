package src.Coordinator.model;

import java.util.ArrayList;
import java.util.List;

public class Session {
    private String sessionId;
    private String date;
    private String time;
    private String venue;
    private String sessionType;  // "Oral" or "Poster"
    private List<String> studentIds;
    private List<String> evaluatorIds;
    private int maxCapacity;
    
    public Session(String sessionId, String date, String time, String venue, 
                  String sessionType, int maxCapacity) {
        this.sessionId = sessionId;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.sessionType = sessionType;
        this.maxCapacity = maxCapacity;
        this.studentIds = new ArrayList<>();
        this.evaluatorIds = new ArrayList<>();
    }
    
    // Getters
    public String getSessionId() { return sessionId; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getVenue() { return venue; }
    public String getSessionType() { return sessionType; }
    public List<String> getStudentIds() { return studentIds; }
    public List<String> getEvaluatorIds() { return evaluatorIds; }
    public int getMaxCapacity() { return maxCapacity; }
    
    // Setters for update
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setVenue(String venue) { this.venue = venue; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }
    
    // Methods
    public void addStudent(String studentId) {
        if (studentIds.size() < maxCapacity && !studentIds.contains(studentId)) {
            studentIds.add(studentId);
        }
    }
    
    public void addEvaluator(String evaluatorId) {
        if (!evaluatorIds.contains(evaluatorId)) {
            evaluatorIds.add(evaluatorId);
        }
    }
    
    public boolean isFull() {
        return studentIds.size() >= maxCapacity;
    }
    
    public String toFileString() {
        String studentStr = String.join(",", studentIds);
        String evaluatorStr = String.join(",", evaluatorIds);
        return sessionId + "|" + date + "|" + time + "|" + venue + "|" + 
               sessionType + "|" + maxCapacity + "|" + studentStr + "|" + evaluatorStr;
    }
}