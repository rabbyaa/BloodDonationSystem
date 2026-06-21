package blooddonationsystem;

public class AuthManager {

    private User currentUser = null;

    public boolean login(String email, String password) {
        User user = FileStorageManager.findUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public boolean register(String fullName, String email, String password,
                            String phone, String bloodGroup, String university) {
        if (FileStorageManager.emailExists(email)) return false;
        String id = FileStorageManager.generateUserId();
        User user = new User(id, fullName, email, password, phone, bloodGroup, university, "USER");
        FileStorageManager.addUser(user);
        currentUser = user;
        return true;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean isLoggedIn()  { return currentUser != null; }
    public User getCurrentUser() { return currentUser; }
    public boolean isAdmin()     { return currentUser != null && currentUser.getRole().equals("ADMIN"); }
}
