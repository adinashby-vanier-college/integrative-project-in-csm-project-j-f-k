package project.optics.jfkt.models;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.nio.file.attribute.PosixFilePermission;

public class UserManager {
    public record User(String username, String encryptedPassword) {
        public User {
            username = username.trim();
            if (username.isEmpty() || encryptedPassword == null) {
                throw new IllegalArgumentException("Invalid user data");
            }
        }
    }

    private static final String DATA_DIR = System.getProperty("user.home") + "/.optics_app";
    private static final String USER_DATA_FILE = DATA_DIR + "/user_credential.txt";
    private final Map<String, User> users;

    public UserManager() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            setFilePermissions();
        } catch (IOException e) {
            System.err.println("Could not create data directory: " + e.getMessage());
        }
        this.users = loadUsers();

    }

    private void setFilePermissions() throws IOException {
        Path path = Paths.get(DATA_DIR);
        if (Files.exists(path)) {
            try {
                Files.setPosixFilePermissions(path, EnumSet.of(
                        PosixFilePermission.OWNER_READ,
                        PosixFilePermission.OWNER_WRITE,
                        PosixFilePermission.OWNER_EXECUTE
                ));
            } catch (UnsupportedOperationException e) {

            }
        }
    }

    private Map<String, User> loadUsers() {
        Map<String, User> loadedUsers = new ConcurrentHashMap<>();
        Path path = Paths.get(USER_DATA_FILE);

        if (!Files.exists(path)) return loadedUsers;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                if (parts.length == 2) {
                    loadedUsers.put(parts[0], new User(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return loadedUsers;
    }

    private void saveUsers() {
        Path path = Paths.get(USER_DATA_FILE);
        try {
            BufferedWriter writer = Files.newBufferedWriter(path,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

            for (User user : users.values()) {
                writer.write(user.username() + "|" + user.encryptedPassword());
                writer.newLine();
            }
            writer.close();

            // Set secure file permissions
            try {
                Files.setPosixFilePermissions(path, EnumSet.of(
                        PosixFilePermission.OWNER_READ,
                        PosixFilePermission.OWNER_WRITE
                ));
            } catch (UnsupportedOperationException e) {
                // Windows system
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }



    public User createUser(String username, String encryptedPassword) {
        User newUser = new User(username, encryptedPassword);
        if (users.putIfAbsent(newUser.username(), newUser) != null) {
            throw new IllegalArgumentException("Username '" + username + "' already exists");
        }
        saveUsers();
        return newUser;
    }

    public Optional<User> findUser(String username) {
        return Optional.ofNullable(users.get(normalizeUsername(username)));
    }

    public boolean updatePassword(String username, String newEncryptedPassword) {
        User existing = findUser(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        users.put(username, new User(username, newEncryptedPassword));
        saveUsers();
        return true;
    }

    public boolean deleteUser(String username) {
        boolean removed = users.remove(normalizeUsername(username)) != null;
        if (removed) saveUsers();
        return removed;
    }

    private String normalizeUsername(String username) {
        return username == null ? null : username.trim();
    }
}