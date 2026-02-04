package model;

public class Student extends User { // Assuming User is the base class
    private String studentId;
    private String name;

    public Student(String id, String username, String password, String name) {
        super(id, username, password, "Student");
        this.studentId = id;
        this.name = name;
    }

    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    
    // toString for file saving
    @Override
    public String toString() {
        return getId() + "," + getUsername() + "," + getPassword() + "," + getName();
    }
}