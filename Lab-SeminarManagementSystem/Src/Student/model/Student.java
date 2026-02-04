package src.Student.model;

import src.common.model.User;

public class Student extends User {
    private String studentId;
    private String name;

    public Student(String id, String username, String password, String name) {
        super(id, username, password, "Student");
        this.studentId = id;
        this.name = name;
    }

    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    
    @Override
    public String toString() {
        return getId() + "," + getUsername() + "," + getPassword() + "," + getName();
    }
}