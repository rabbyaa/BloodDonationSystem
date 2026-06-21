package blooddonationsystem;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileStorageManager.initialize();
            AuthManager authManager = new AuthManager();
            new SplashScreen(authManager).setVisible(true);
        });
    }
}
