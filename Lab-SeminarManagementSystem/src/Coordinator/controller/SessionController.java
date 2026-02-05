package src.Coordinator.controller;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import src.Coordinator.model.Session;

public class SessionController {
    private final String SESSION_FILE = "sessions.txt";
    private final String ASSIGNMENT_FILE = "assignments.txt";
    
    // Create new session with validation
    public boolean createSession(Session session) {
        // Check for time conflicts
        if (hasTimeConflicts(session)) {
            return false;
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SESSION_FILE, true))) {
            writer.write(session.toFileString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Check for time conflicts (evaluator availability)
    private boolean hasTimeConflicts(Session newSession) {
        List<Session> sessions = getAllSessions();
        
        for (Session existingSession : sessions) {
            // Skip if same session (for updates)
            if (existingSession.getSessionId().equals(newSession.getSessionId())) {
                continue;
            }
            
            // Check if sessions overlap
            if (newSession.overlapsWith(existingSession)) {
                // Check for overlapping evaluators
                for (String evaluatorId : newSession.getEvaluatorIds()) {
                    if (existingSession.hasEvaluator(evaluatorId)) {
                        return true; // Conflict found
                    }
                }
            }
        }
        return false; // No conflicts
    }
    
    // Get all sessions
    public List<Session> getAllSessions() {
        List<Session> sessions = new ArrayList<>();
        File file = new File(SESSION_FILE);
        if (!file.exists()) return sessions;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 8) {
                    Session session = new Session(parts[0], parts[1], parts[2], 
                                                 parts[3], parts[4], Integer.parseInt(parts[5]));
                    
                    // Add students
                    if (!parts[6].isEmpty()) {
                        String[] studentIds = parts[6].split(",");
                        for (String id : studentIds) {
                            session.addStudent(id);
                        }
                    }
                    
                    // Add evaluators
                    if (!parts[7].isEmpty()) {
                        String[] evaluatorIds = parts[7].split(",");
                        for (String id : evaluatorIds) {
                            session.addEvaluator(id);
                        }
                    }
                    
                    sessions.add(session);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sessions;
    }
    
    // Update session with validation
    public boolean updateSession(Session updatedSession) {
        // Check for time conflicts (excluding current session)
        List<Session> sessions = getAllSessions();
        boolean found = false;
        
        for (int i = 0; i < sessions.size(); i++) {
            if (sessions.get(i).getSessionId().equals(updatedSession.getSessionId())) {
                sessions.set(i, updatedSession);
                found = true;
                break;
            }
        }
        
        if (!found) {
            sessions.add(updatedSession);
        }
        
        // Check for conflicts
        for (Session session : sessions) {
            if (!session.getSessionId().equals(updatedSession.getSessionId())) {
                if (updatedSession.overlapsWith(session)) {
                    for (String evaluatorId : updatedSession.getEvaluatorIds()) {
                        if (session.hasEvaluator(evaluatorId)) {
                            return false; // Conflict found
                        }
                    }
                }
            }
        }
        
        return saveAllSessions(sessions);
    }
    
    // Get sessions by evaluator
    public List<Session> getSessionsByEvaluator(String evaluatorId) {
        List<Session> evaluatorSessions = new ArrayList<>();
        for (Session session : getAllSessions()) {
            if (session.hasEvaluator(evaluatorId)) {
                evaluatorSessions.add(session);
            }
        }
        return evaluatorSessions;
    }
    
    // Check if evaluator is available
    public boolean isEvaluatorAvailable(String evaluatorId, String startDateTime, String endDateTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date newStart = sdf.parse(startDateTime);
            Date newEnd = sdf.parse(endDateTime);
            
            for (Session session : getSessionsByEvaluator(evaluatorId)) {
                Date existingStart = sdf.parse(session.getStartDateTime());
                Date existingEnd = sdf.parse(session.getEndDateTime());
                
                // Check for overlap
                if (newStart.before(existingEnd) && newEnd.after(existingStart)) {
                    return false; // Not available
                }
            }
            return true; // Available
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Save all sessions to file
    private boolean saveAllSessions(List<Session> sessions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SESSION_FILE))) {
            for (Session session : sessions) {
                writer.write(session.toFileString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete session
    public boolean deleteSession(String sessionId) {
        List<Session> sessions = getAllSessions();
        sessions.removeIf(s -> s.getSessionId().equals(sessionId));
        return saveAllSessions(sessions);
    }
    
    // Assign student to session
    public boolean assignStudentToSession(String studentId, String sessionId) {
        List<Session> sessions = getAllSessions();
        
        for (Session session : sessions) {
            if (session.getSessionId().equals(sessionId)) {
                if (session.isFull()) {
                    return false; // Session is full
                }
                session.addStudent(studentId);
                return saveAllSessions(sessions);
            }
        }
        return false; // Session not found
    }
    
    // Assign evaluator to session
    public boolean assignEvaluatorToSession(String evaluatorId, String sessionId) {
        List<Session> sessions = getAllSessions();
        
        for (Session session : sessions) {
            if (session.getSessionId().equals(sessionId)) {
                // Check if evaluator is available
                if (!isEvaluatorAvailable(evaluatorId, session.getStartDateTime(), session.getEndDateTime())) {
                    return false;
                }
                session.addEvaluator(evaluatorId);
                return saveAllSessions(sessions);
            }
        }
        return false;
    }
    
    // Assign evaluator to student (creates assignment record)
    public boolean assignEvaluatorToStudent(String evaluatorId, String studentId, String sessionId) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ASSIGNMENT_FILE, true))) {
            String assignmentId = UUID.randomUUID().toString().substring(0, 8);
            writer.write(assignmentId + "|" + sessionId + "|" + studentId + "|" + evaluatorId);
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get assignments for a student
    public Map<String, String> getStudentAssignment(String studentId) {
        Map<String, String> assignment = new HashMap<>();
        File file = new File(ASSIGNMENT_FILE);
        
        if (!file.exists()) {
            return assignment;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4 && parts[2].equals(studentId)) {
                    assignment.put("sessionId", parts[1]);
                    assignment.put("evaluatorId", parts[3]);
                    assignment.put("assignmentId", parts[0]);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return assignment;
    }
}