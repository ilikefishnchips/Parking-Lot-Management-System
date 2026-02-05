package src.Student.controller;

import src.Student.model.Student;
import java.io.*;
import java.util.*;

public class StudentDataController {
    private final String STUDENTS_FILE = "students_data.txt";
    
    // Save or update student data
    public boolean saveStudentData(Student student) {
        List<Student> allStudents = loadAllStudents();
        
        // Remove old entry if exists
        allStudents.removeIf(s -> s.getStudentId().equals(student.getStudentId()));
        
        // Add updated student
        allStudents.add(student);
        
        // Save all students
        return saveAllStudents(allStudents);
    }
    
    // Load student by ID
    public Student loadStudent(String studentId) {
        List<Student> students = loadAllStudents();
        for (Student student : students) {
            if (student.getStudentId().equals(studentId)) {
                return student;
            }
        }
        return null;
    }
    
    // Load all students
    public List<Student> loadAllStudents() {
        List<Student> students = new ArrayList<>();
        File file = new File(STUDENTS_FILE);
        
        if (!file.exists()) {
            return students;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Student student = Student.fromString(line);
                if (student != null) {
                    students.add(student);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return students;
    }
    
    // Get unassigned students
    public List<Student> getUnassignedStudents() {
        List<Student> allStudents = loadAllStudents();
        List<Student> unassigned = new ArrayList<>();
        
        for (Student student : allStudents) {
            if (!student.isAssigned()) {
                unassigned.add(student);
            }
        }
        
        return unassigned;
    }
    
    // Get students by preferred type
    public List<Student> getStudentsByPreferredType(String presentationType) {
        List<Student> allStudents = loadAllStudents();
        List<Student> filtered = new ArrayList<>();
        
        for (Student student : allStudents) {
            if (student.prefersSessionType(presentationType) && !student.isAssigned()) {
                filtered.add(student);
            }
        }
        
        return filtered;
    }
    
    // Assign student to session
    public boolean assignStudentToSession(String studentId, String sessionId, String evaluatorId) {
        Student student = loadStudent(studentId);
        if (student == null) {
            return false;
        }
        
        student.setAssignedSessionId(sessionId);
        student.setAssignedEvaluatorId(evaluatorId);
        
        return saveStudentData(student);
    }
    
    // Remove student assignment
    public boolean removeStudentAssignment(String studentId) {
        Student student = loadStudent(studentId);
        if (student == null) {
            return false;
        }
        
        student.clearAssignments();
        return saveStudentData(student);
    }
    
    // Save all students to file
    private boolean saveAllStudents(List<Student> students) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STUDENTS_FILE))) {
            for (Student student : students) {
                writer.write(student.toString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get students assigned to specific session
    public List<Student> getStudentsBySession(String sessionId) {
        List<Student> allStudents = loadAllStudents();
        List<Student> sessionStudents = new ArrayList<>();
        
        for (Student student : allStudents) {
            if (sessionId.equals(student.getAssignedSessionId())) {
                sessionStudents.add(student);
            }
        }
        
        return sessionStudents;
    }
    
    // Check if student exists
    public boolean studentExists(String studentId) {
        return loadStudent(studentId) != null;
    }
}