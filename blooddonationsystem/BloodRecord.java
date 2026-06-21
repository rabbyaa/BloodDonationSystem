package blooddonationsystem;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BloodRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type { DONATION, REQUEST }
    public enum Status { PENDING, COMPLETED, REJECTED }

    private String recordId;
    private String userId;
    private String userName;
    private String userPhone;
    private String userEmail;
    private String bloodGroup;
    private String location;
    private String hospital;
    private String message;
    private Type type;
    private Status status;
    private String timestamp;
    private int aiSuspicionScore;   // 0-100, higher = more suspicious
    private String aiReason;
    private boolean flaggedByAdmin;

    public BloodRecord(String recordId, String userId, String userName, String userPhone,
                       String userEmail, String bloodGroup, String location,
                       String hospital, String message, Type type) {
        this.recordId = recordId;
        this.userId = userId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userEmail = userEmail;
        this.bloodGroup = bloodGroup;
        this.location = location;
        this.hospital = hospital;
        this.message = message;
        this.type = type;
        this.status = Status.PENDING;
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.aiSuspicionScore = 0;
        this.aiReason = "Not analyzed yet";
        this.flaggedByAdmin = false;
    }

    // Getters
    public String getRecordId()       { return recordId; }
    public String getUserId()         { return userId; }
    public String getUserName()       { return userName; }
    public String getUserPhone()      { return userPhone; }
    public String getUserEmail()      { return userEmail; }
    public String getBloodGroup()     { return bloodGroup; }
    public String getLocation()       { return location; }
    public String getHospital()       { return hospital; }
    public String getMessage()        { return message; }
    public Type   getType()           { return type; }
    public Status getStatus()         { return status; }
    public String getTimestamp()      { return timestamp; }
    public int    getAiSuspicionScore(){ return aiSuspicionScore; }
    public String getAiReason()       { return aiReason; }
    public boolean isFlaggedByAdmin() { return flaggedByAdmin; }

    // Setters
    public void setStatus(Status s)          { this.status = s; }
    public void setAiSuspicionScore(int s)   { this.aiSuspicionScore = s; }
    public void setAiReason(String r)        { this.aiReason = r; }
    public void setFlaggedByAdmin(boolean f) { this.flaggedByAdmin = f; }

    public String getTypeLabel()   { return type == Type.DONATION ? "Donation" : "Request"; }
    public String getStatusLabel() {
        switch (status) {
            case PENDING:   return "Pending";
            case COMPLETED: return "Completed";
            case REJECTED:  return "Rejected";
            default:        return "Unknown";
        }
    }

    public String getRiskLabel() {
        if (aiSuspicionScore >= 70) return "HIGH RISK";
        if (aiSuspicionScore >= 40) return "MEDIUM RISK";
        return "LOW RISK";
    }
}
