package src.common.model;

import java.util.ArrayList;
import java.util.List;

public class Report {
    private String reportId;
    private String reportType;  // "Schedule", "Evaluation", "Award"
    private String generatedDate;
    private List<String> contentLines;
    
    public Report(String reportId, String reportType, String generatedDate) {
        this.reportId = reportId;
        this.reportType = reportType;
        this.generatedDate = generatedDate;
        this.contentLines = new ArrayList<>();
    }
    
    // Getters
    public String getReportId() { return reportId; }
    public String getReportType() { return reportType; }
    public String getGeneratedDate() { return generatedDate; }
    public List<String> getContentLines() { return contentLines; }
    
    // Methods
    public void addContentLine(String line) {
        contentLines.add(line);
    }
    
    public void clearContent() {
        contentLines.clear();
    }
    
    public String getFormattedContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(reportType).append(" Report ===\n");
        sb.append("Generated on: ").append(generatedDate).append("\n");
        sb.append("Report ID: ").append(reportId).append("\n");
        sb.append("----------------------------------------\n");
        
        for (String line : contentLines) {
            sb.append(line).append("\n");
        }
        
        return sb.toString();
    }
}