package src.Coordinator.model;

import src.common.model.User;

public class Coordinator extends User {
    private String name;
    private String contactEmail;
    
    public Coordinator(String id, String username, String password, 
                      String name, String contactEmail) {
        super(id, username, password, "Coordinator");
        this.name = name;
        this.contactEmail = contactEmail;
    }
    
    public String getName() { return name; }
    public String getContactEmail() { return contactEmail; }
    
    @Override
    public String toString() {
        return getId() + "," + getUsername() + "," + getPassword() + "," + 
               name + "," + contactEmail;
    }
}