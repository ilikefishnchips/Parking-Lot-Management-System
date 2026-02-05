package src.Coordinator.controller;

import java.io.*;
import java.util.*;
import src.common.model.Board;
import src.common.model.Submission;

public class BoardController {
    private final String BOARD_FILE = "boards.txt";
    private final String SUBMISSION_FILE = "submissions.txt";
    
    // Create a new board
    public boolean createBoard(Board board) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOARD_FILE, true))) {
            writer.write(board.toFileString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all boards
    public List<Board> getAllBoards() {
        List<Board> boards = new ArrayList<>();
        File file = new File(BOARD_FILE);
        if (!file.exists()) return boards;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 8) {
                    Board board = new Board(parts[0], parts[1], parts[4], 
                                          Integer.parseInt(parts[5]), 
                                          Integer.parseInt(parts[6]), 
                                          parts[7]);
                    board.setStatus(parts[2]);
                    if (!parts[3].equals("NONE")) {
                        board.setPosterId(parts[3]);
                    }
                    boards.add(board);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return boards;
    }
    
    // Get boards for a specific session
    public List<Board> getBoardsForSession(String sessionId) {
        List<Board> sessionBoards = new ArrayList<>();
        for (Board board : getAllBoards()) {
            if (board.getSessionId().equals(sessionId)) {
                sessionBoards.add(board);
            }
        }
        return sessionBoards;
    }
    
    // Assign poster to board
    public boolean assignPosterToBoard(String posterId, String boardId) {
        List<Board> boards = getAllBoards();
        boolean updated = false;
        
        for (Board board : boards) {
            if (board.getBoardId().equals(boardId)) {
                board.setPosterId(posterId);
                board.setStatus("Occupied");
                updated = true;
                break;
            }
        }
        
        if (updated) {
            return saveAllBoards(boards);
        }
        return false;
    }
    
    // Unassign poster from board
    public boolean unassignPosterFromBoard(String boardId) {
        List<Board> boards = getAllBoards();
        boolean updated = false;
        
        for (Board board : boards) {
            if (board.getBoardId().equals(boardId)) {
                board.setPosterId(null);
                board.setStatus("Available");
                updated = true;
                break;
            }
        }
        
        if (updated) {
            return saveAllBoards(boards);
        }
        return false;
    }
    
    // Get poster submissions for a session
    public List<Submission> getPosterSubmissionsForSession(String sessionId) {
        List<Submission> submissions = new ArrayList<>();
        File file = new File(SUBMISSION_FILE);
        if (!file.exists()) return submissions;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 7) {
                    String type = parts[5];
                    if (type.toLowerCase().contains("poster")) {
                        // Check if this poster is already assigned
                        boolean alreadyAssigned = false;
                        if (parts.length > 7 && !parts[7].equals("NONE")) {
                            alreadyAssigned = true;
                        }
                        
                        if (!alreadyAssigned) {
                            Submission sub = new Submission(parts[0], parts[1], parts[2], 
                                                          parts[3], parts[4], parts[5], parts[6]);
                            submissions.add(sub);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return submissions;
    }
    
    // Save all boards
    private boolean saveAllBoards(List<Board> boards) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOARD_FILE))) {
            for (Board board : boards) {
                writer.write(board.toFileString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get available boards for a session
    public List<Board> getAvailableBoards(String sessionId) {
        List<Board> available = new ArrayList<>();
        for (Board board : getBoardsForSession(sessionId)) {
            if (board.getStatus().equals("Available")) {
                available.add(board);
            }
        }
        return available;
    }
    
    // Generate board layout report
    public String generateBoardLayoutReport(String sessionId) {
        List<Board> boards = getBoardsForSession(sessionId);
        StringBuilder report = new StringBuilder();
        
        report.append("POSTER BOARD LAYOUT - SESSION: ").append(sessionId).append("\n");
        report.append("========================================\n");
        
        // Group by location
        Map<String, List<Board>> locationMap = new TreeMap<>();
        for (Board board : boards) {
            locationMap.putIfAbsent(board.getLocation(), new ArrayList<>());
            locationMap.get(board.getLocation()).add(board);
        }
        
        for (Map.Entry<String, List<Board>> entry : locationMap.entrySet()) {
            report.append("\nLocation: ").append(entry.getKey()).append("\n");
            report.append("------------------------------------------------\n");
            
            for (Board board : entry.getValue()) {
                String posterInfo = board.getPosterId() != null ? 
                    "Poster: " + board.getPosterId() : "Empty";
                report.append(String.format("  Board %s: %s | Size: %dx%d | Status: %s\n",
                    board.getBoardId(), posterInfo, 
                    board.getWidth(), board.getHeight(), board.getStatus()));
            }
        }
        
        return report.toString();
    }
    
    // Get board by ID
    public Board getBoardById(String boardId) {
        for (Board board : getAllBoards()) {
            if (board.getBoardId().equals(boardId)) {
                return board;
            }
        }
        return null;
    }
    
    // Update submission file with board assignment
    public boolean updateSubmissionWithBoard(String submissionId, String boardId) {
        List<String> lines = new ArrayList<>();
        File file = new File(SUBMISSION_FILE);
        
        if (!file.exists()) return false;
        
        boolean updated = false;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 7 && parts[0].equals(submissionId)) {
                    // Update this line with board assignment
                    if (parts.length == 7) {
                        line = line + "|" + boardId;
                    } else if (parts.length == 8) {
                        parts[7] = boardId;
                        line = String.join("|", parts);
                    }
                    updated = true;
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        if (updated) {
            // Write back all lines
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        
        return false;
    }
}