package blooddonationsystem;

import java.io.*;
import java.util.*;

public class FileStorageManager {

    private static final String DATA_DIR    = "bloodbank_data";
    private static final String USERS_FILE  = DATA_DIR + "/users.dat";
    private static final String RECORDS_FILE= DATA_DIR + "/records.dat";

    // ─── Init ────────────────────────────────────────────────────────────────
    public static void initialize() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();

        // Seed default admin if no users exist
        List<User> users = loadUsers();
        boolean hasAdmin = users.stream().anyMatch(u -> u.getRole().equals("ADMIN"));
        if (!hasAdmin) {
            User admin = new User(
                "ADMIN001", "System Admin", "admin@bloodbank.com",
                "admin123", "0300-0000000", "O+", "System", "ADMIN"
            );
            users.add(admin);
            saveUsers(users);
        }
    }

    // ─── USER OPERATIONS ─────────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public static List<User> loadUsers() {
        File f = new File(USERS_FILE);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (List<User>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void saveUsers(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addUser(User user) {
        List<User> users = loadUsers();
        users.add(user);
        saveUsers(users);
    }

    public static User findUserByEmail(String email) {
        return loadUsers().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst().orElse(null);
    }

    public static boolean emailExists(String email) {
        return findUserByEmail(email) != null;
    }

    public static void updateUser(User updated) {
        List<User> users = loadUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(updated.getUserId())) {
                users.set(i, updated);
                break;
            }
        }
        saveUsers(users);
    }

    // ─── RECORD OPERATIONS ───────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    public static List<BloodRecord> loadRecords() {
        File f = new File(RECORDS_FILE);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (List<BloodRecord>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void saveRecords(List<BloodRecord> records) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RECORDS_FILE))) {
            oos.writeObject(records);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addRecord(BloodRecord record) {
        List<BloodRecord> records = loadRecords();
        records.add(record);
        saveRecords(records);
    }

    public static void updateRecord(BloodRecord updated) {
        List<BloodRecord> records = loadRecords();
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getRecordId().equals(updated.getRecordId())) {
                records.set(i, updated);
                break;
            }
        }
        saveRecords(records);
    }

    public static List<BloodRecord> getRecordsByUser(String userId) {
        List<BloodRecord> result = new ArrayList<>();
        for (BloodRecord r : loadRecords()) {
            if (r.getUserId().equals(userId)) result.add(r);
        }
        return result;
    }

    public static List<BloodRecord> searchRecords(String query, String typeFilter, String statusFilter) {
        String q = query.toLowerCase().trim();
        List<BloodRecord> result = new ArrayList<>();
        for (BloodRecord r : loadRecords()) {
            boolean matchesQuery = q.isEmpty()
                || r.getUserName().toLowerCase().contains(q)
                || r.getBloodGroup().toLowerCase().contains(q)
                || r.getLocation().toLowerCase().contains(q)
                || r.getHospital().toLowerCase().contains(q)
                || r.getUserEmail().toLowerCase().contains(q)
                || r.getMessage().toLowerCase().contains(q);

            boolean matchesType = typeFilter.equals("ALL")
                || r.getTypeLabel().equalsIgnoreCase(typeFilter);

            boolean matchesStatus = statusFilter.equals("ALL")
                || r.getStatusLabel().equalsIgnoreCase(statusFilter);

            if (matchesQuery && matchesType && matchesStatus) result.add(r);
        }
        return result;
    }

    public static String generateUserId() {
        return "USR" + System.currentTimeMillis();
    }

    public static String generateRecordId() {
        return "REC" + System.currentTimeMillis();
    }
}
