package src.Coordinator.controller;

import src.Coordinator.model.Session;
import java.io.*;
import java.util.*;
import java.util.UUID;

public class SessionController {
    private final String SESSION_FILE = "sessions.txt";
    private final String ASSIGNMENT_FILE = "assignments.txt";
    
    // Create new session
    public boolean createSession(Session session) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SESSION_FILE, true))) {
            writer.write(session.toFileString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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
    
    // Update session
    public boolean updateSession(Session updatedSession) {
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
        
        return saveAllSessions(sessions);
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
                session.addEvaluator(evaluatorId);
                return saveAllSessions(sessions);
            }
        }
        return false;
    }
    
    // Assign evaluator to student
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
}