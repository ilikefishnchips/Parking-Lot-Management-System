package src.Student.model;

import java.util.ArrayList;
import java.util.List;
import src.common.model.User;

public class Student extends User {
    private String studentId;
    private String name;
    private String preferredPresentationType;
    private List<String> unavailableTimes; // Times when student cannot present
    private String assignedSessionId;
    private String assignedEvaluatorId;
    
    public Student(String id, String username, String password, String name) {
        super(id, username, password, "Student");
        this.studentId = id;
        this.name = name;
        this.preferredPresentationType = "";
        this.unavailableTimes = new ArrayList<>();
        this.assignedSessionId = "";
        this.assignedEvaluatorId = "";
    }
    
    // Getters
    public String getStudentId() { 
        return studentId; 
    }
    
    public String getName() { 
        return name; 
    }
    
    public String getPreferredPresentationType() { 
        return preferredPresentationType; 
    }
    
    public List<String> getUnavailableTimes() { 
        return new ArrayList<>(unavailableTimes); 
    }
    
    public String getAssignedSessionId() { 
        return assignedSessionId; 
    }
    
    public String getAssignedEvaluatorId() { 
        return assignedEvaluatorId; 
    }
    
    // Setters
    public void setPreferredPresentationType(String type) { 
        this.preferredPresentationType = type; 
    }
    
    public void addUnavailableTime(String timeSlot) { 
        if (!unavailableTimes.contains(timeSlot)) {
            unavailableTimes.add(timeSlot);
        }
    }
    
    public void clearUnavailableTimes() {
        unavailableTimes.clear();
    }
    
    public void setAssignedSessionId(String sessionId) { 
        this.assignedSessionId = sessionId; 
    }
    
    public void setAssignedEvaluatorId(String evaluatorId) { 
        this.assignedEvaluatorId = evaluatorId; 
    }
    
    // Clear all assignments
    public void clearAssignments() {
        this.assignedSessionId = "";
        this.assignedEvaluatorId = "";
    }
    
    // Check if student is assigned
    public boolean isAssigned() {
        return !assignedSessionId.isEmpty() && !assignedEvaluatorId.isEmpty();
    }
    
    // Check if student has submitted preferences
    public boolean hasSubmittedPreferences() {
        return !preferredPresentationType.isEmpty();
    }
    
    // Check if student is available for session at specific time
    public boolean isAvailableForSession(String sessionDateTime) {
        // Check if student is unavailable during this time
        return !unavailableTimes.contains(sessionDateTime);
    }
    
    // Check if student prefers this session type
    public boolean prefersSessionType(String sessionType) {
        if (preferredPresentationType.isEmpty()) {
            return true; // No preference set, so accept any
        }
        
        // Map Oral Presentation -> Oral, Poster Presentation -> Poster
        String studentPref = preferredPresentationType.toLowerCase();
        String sessionTypeLower = sessionType.toLowerCase();
        
        if (studentPref.contains("oral") && sessionTypeLower.contains("oral")) {
            return true;
        }
        if (studentPref.contains("poster") && sessionTypeLower.contains("poster")) {
            return true;
        }
        
        return false;
    }
    
    // Get student status as string
    public String getStatus() {
        if (!isAssigned()) {
            return "Not Assigned";
        }
        return "Assigned to Session: " + assignedSessionId;
    }
    
    // Convert to string for display
    public String getDisplayInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" (").append(studentId).append(")");
        
        if (!preferredPresentationType.isEmpty()) {
            sb.append(" - Prefers: ").append(preferredPresentationType);
        }
        
        if (!assignedSessionId.isEmpty()) {
            sb.append(" - Assigned to: ").append(assignedSessionId);
        }
        
        if (!assignedEvaluatorId.isEmpty()) {
            sb.append(" - Evaluator: ").append(assignedEvaluatorId);
        }
        
        return sb.toString();
    }
    
    // Parse from string (for file loading)
    public static Student fromString(String data) {
        String[] parts = data.split("\\|");
        if (parts.length >= 7) {
            Student student = new Student(parts[0], parts[1], parts[2], parts[3]);
            
            if (parts.length > 4 && !parts[4].isEmpty()) {
                student.setPreferredPresentationType(parts[4]);
            }
            
            if (parts.length > 5 && !parts[5].isEmpty()) {
                String[] times = parts[5].split(";");
                for (String time : times) {
                    student.addUnavailableTime(time);
                }
            }
            
            if (parts.length > 6 && !parts[6].isEmpty()) {
                student.setAssignedSessionId(parts[6]);
            }
            
            if (parts.length > 7 && !parts[7].isEmpty()) {
                student.setAssignedEvaluatorId(parts[7]);
            }
            
            return student;
        }
        return null;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(studentId).append("|")
          .append(getUsername()).append("|")
          .append(getPassword()).append("|")
          .append(name).append("|")
          .append(preferredPresentationType).append("|")
          .append(String.join(";", unavailableTimes)).append("|")
          .append(assignedSessionId).append("|")
          .append(assignedEvaluatorId);
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Student student = (Student) obj;
        return studentId.equals(student.studentId);
    }
    
    @Override
    public int hashCode() {
        return studentId.hashCode();
    }
    
    // Additional helper methods
    
    // Check if student can be assigned to session (based on preferences and availability)
    public boolean canBeAssignedToSession(String sessionId, String sessionType, String sessionDateTime) {
        // Check if already assigned
        if (isAssigned() && assignedSessionId.equals(sessionId)) {
            return true; // Already assigned to this session
        }
        
        // Check if already assigned to another session
        if (!assignedSessionId.isEmpty() && !assignedSessionId.equals(sessionId)) {
            return false; // Already assigned to different session
        }
        
        // Check preference match
        if (!prefersSessionType(sessionType)) {
            return false;
        }
        
        // Check availability
        return isAvailableForSession(sessionDateTime);
    }
    
    // Get a summary for coordinator view
    public String getCoordinatorSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Student ID: ").append(studentId).append("\n");
        sb.append("Name: ").append(name).append("\n");
        sb.append("Preferred Type: ").append(preferredPresentationType.isEmpty() ? "Not specified" : preferredPresentationType).append("\n");
        sb.append("Unavailable Times: ").append(unavailableTimes.isEmpty() ? "None" : String.join(", ", unavailableTimes)).append("\n");
        sb.append("Assigned Session: ").append(assignedSessionId.isEmpty() ? "Not assigned" : assignedSessionId).append("\n");
        sb.append("Assigned Evaluator: ").append(assignedEvaluatorId.isEmpty() ? "Not assigned" : assignedEvaluatorId).append("\n");
        return sb.toString();
    }
}