package blooddonationsystem;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String bloodGroup;
    private String university;
    private String role; // "USER" or "ADMIN"
    private boolean verified;

    public User(String userId, String fullName, String email, String password,
                String phone, String bloodGroup, String university, String role) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.bloodGroup = bloodGroup;
        this.university = university;
        this.role = role;
        this.verified = role.equals("ADMIN");
    }

    public String getUserId()    { return userId; }
    public String getFullName()  { return fullName; }
    public String getEmail()     { return email; }
    public String getPassword()  { return password; }
    public String getPhone()     { return phone; }
    public String getBloodGroup(){ return bloodGroup; }
    public String getUniversity(){ return university; }
    public String getRole()      { return role; }
    public boolean isVerified()  { return verified; }
    public void setVerified(boolean v) { this.verified = v; }
    public void setPassword(String p)  { this.password = p; }

    @Override
    public String toString() {
        return fullName + " (" + email + ") | " + bloodGroup + " | " + university;
    }
}
