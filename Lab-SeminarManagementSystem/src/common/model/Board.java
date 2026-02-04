package src.common.model;

public class Board {
    private final String boardId;
    private String location;
    private String status;
    private String posterId;
    private final String sessionId;
    private final int width;
    private final int height;
    private final String specialRequirements;
    
    public Board(String boardId, String location, String sessionId, 
                 int width, int height, String requirements) {
        this.boardId = boardId;
        this.location = location;
        this.status = "Available";
        this.posterId = null;
        this.sessionId = sessionId;
        this.width = width;
        this.height = height;
        this.specialRequirements = requirements;
    }
    
    // Getters
    public String getBoardId() { return boardId; }
    public String getLocation() { return location; }
    public String getStatus() { return status; }
    public String getPosterId() { return posterId; }
    public String getSessionId() { return sessionId; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public String getSpecialRequirements() { return specialRequirements; }
    
    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setPosterId(String posterId) { this.posterId = posterId; }
    public void setLocation(String location) { this.location = location; }
    
    public String toFileString() {
        return boardId + "|" + location + "|" + status + "|" + 
               (posterId != null ? posterId : "NONE") + "|" + 
               sessionId + "|" + width + "|" + height + "|" + specialRequirements;
    }
    
    public String getDisplayInfo() {
        return String.format("Board %s - %s (%dcm x %dcm) - %s", 
               boardId, location, width, height, status);
    }
}