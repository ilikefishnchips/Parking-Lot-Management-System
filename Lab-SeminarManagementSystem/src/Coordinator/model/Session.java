package src.Coordinator.model;

import java.util.ArrayList;
import java.util.List;

public class Session {
    private String sessionId;
    private String startDateTime;  // Changed from date
    private String endDateTime;    // Added end date/time
    private String venue;
    private String sessionType;  // "Oral" or "Poster"
    private List<String> studentIds;
    private List<String> evaluatorIds;
    private int maxCapacity;
    
    public Session(String sessionId, String startDateTime, String endDateTime, String venue, 
                  String sessionType, int maxCapacity) {
        this.sessionId = sessionId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.venue = venue;
        this.sessionType = sessionType;
        this.maxCapacity = maxCapacity;
        this.studentIds = new ArrayList<>();
        this.evaluatorIds = new ArrayList<>();
    }
    
    // Getters
    public String getSessionId() { return sessionId; }
    public String getStartDateTime() { return startDateTime; }
    public String getEndDateTime() { return endDateTime; }
    public String getVenue() { return venue; }
    public String getSessionType() { return sessionType; }
    public List<String> getStudentIds() { return studentIds; }
    public List<String> getEvaluatorIds() { return evaluatorIds; }
    public int getMaxCapacity() { return maxCapacity; }
    
    // For backward compatibility
    public String getDate() { return startDateTime.split(" ")[0]; }
    public String getTime() { return startDateTime.split(" ")[1]; }
    
    // Setters for update
    public void setStartDateTime(String startDateTime) { this.startDateTime = startDateTime; }
    public void setEndDateTime(String endDateTime) { this.endDateTime = endDateTime; }
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
    
    public boolean hasEvaluator(String evaluatorId) {
        return evaluatorIds.contains(evaluatorId);
    }
    
    // Check if session overlaps with another session
    public boolean overlapsWith(Session other) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
            java.util.Date thisStart = sdf.parse(this.startDateTime);
            java.util.Date thisEnd = sdf.parse(this.endDateTime);
            java.util.Date otherStart = sdf.parse(other.startDateTime);
            java.util.Date otherEnd = sdf.parse(other.endDateTime);
            
            return thisStart.before(otherEnd) && thisEnd.after(otherStart);
        } catch (Exception e) {
            return false;
        }
    }
    
    public String toFileString() {
        String studentStr = String.join(",", studentIds);
        String evaluatorStr = String.join(",", evaluatorIds);
        return sessionId + "|" + startDateTime + "|" + endDateTime + "|" + venue + "|" + 
               sessionType + "|" + maxCapacity + "|" + studentStr + "|" + evaluatorStr;
    }
}