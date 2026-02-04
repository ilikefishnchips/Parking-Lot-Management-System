package src.Evaluator.model;

import src.common.model.User;

public class Evaluator extends User {
    private String name;
    private String department;
    
    public Evaluator(String id, String username, String password, String name, String department) {
        super(id, username, password, "Evaluator");
        this.name = name;
        this.department = department;
    }
    
    public String getName() { return name; }
    public String getDepartment() { return department; }
    
    @Override
    public String toString() {
        return getId() + "," + getUsername() + "," + getPassword() + "," + 
               name + "," + department;
    }
}