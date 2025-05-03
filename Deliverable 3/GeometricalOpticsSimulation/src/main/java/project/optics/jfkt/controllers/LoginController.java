// LoginController.java
package project.optics.jfkt.controllers;

import javafx.stage.Stage;
import project.optics.jfkt.models.UserManager;
import project.optics.jfkt.views.CreateAccountView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

public class LoginController {
    public final UserManager userManager;
    protected BigInteger n, e, d;
    protected static final String KEY_FILE = System.getProperty("user.home") + "/.optics_app/rsa_keys.dat";

    public LoginController() {
        this.userManager = new UserManager();
        loadOrGenerateKeys();
    }
    public UserManager getUserManager() {
        return this.userManager;
    }
    public void onLinkClicked(Stage primaryStage) {
        CreateAccountView createAccountView = new CreateAccountView(primaryStage, this);
        primaryStage.getScene().setRoot(createAccountView);
    }

    protected void loadOrGenerateKeys() {
        Path keyPath = Paths.get(KEY_FILE);
        if (Files.exists(keyPath)) {
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(keyPath))) {
                n = (BigInteger) ois.readObject();
                e = (BigInteger) ois.readObject();
                d = (BigInteger) ois.readObject();
                System.out.println("Loaded existing RSA keys");
            } catch (Exception e) {
                System.err.println("Error loading keys, generating new ones: " + e.getMessage());
                generateNewKeys();
            }
        } else {
            generateNewKeys();
        }
    }

    protected void generateNewKeys() {
        SecureRandom random = new SecureRandom();
        BigInteger p = BigInteger.probablePrime(1024, random);
        BigInteger q = BigInteger.probablePrime(1024, random);
        n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        e = BigInteger.valueOf(65537);
        d = e.modInverse(phi);

        // Save the new keys
        try {
            Files.createDirectories(Paths.get(System.getProperty("user.home") + "/.optics_app"));
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get(KEY_FILE)))) {
                oos.writeObject(n);
                oos.writeObject(e);
                oos.writeObject(d);
            }
            System.out.println("Generated and saved new RSA keys");
        } catch (IOException ex) {
            System.err.println("Could not save RSA keys: " + ex.getMessage());
        }
    }

    protected String decrypt(String ciphertext) {
        BigInteger C = new BigInteger(Base64.getDecoder().decode(ciphertext));
        BigInteger M = C.modPow(d, n);
        return new String(M.toByteArray());
    }

    public boolean handleLogin(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            System.out.println("Login attempt with empty credentials");
            return false;
        }

        Optional<UserManager.User> userOpt = userManager.findUser(username);
        if (userOpt.isEmpty()) {
            System.out.println("User not found: " + username);
            return false;
        }

        UserManager.User user = userOpt.get();
        try {
            String decryptedPassword = decrypt(user.encryptedPassword());
            System.out.println("Debug - Stored encrypted: " + user.encryptedPassword());
            System.out.println("Debug - Decrypted password: " + decryptedPassword);
            System.out.println("Debug - Input password: " + password);

            boolean match = password.equals(decryptedPassword);
            System.out.println("Password match: " + match);
            return match;
        } catch (Exception e) {
            System.err.println("Decryption error for user " + username + ": " + e.getMessage());
            return false;
        }
    }

    public boolean updatePassword(String username, String currentPassword, String newPassword) {
        if (!handleLogin(username, currentPassword)) {
            return false;
        }
        try {
            String newEncryptedPassword = encrypt(newPassword);
            return userManager.updatePassword(username, newEncryptedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteUser(String username, String password) {
        if (!handleLogin(username, password)) {
            return false;
        }
        return userManager.deleteUser(username);
    }

    protected String encrypt(String plaintext) {
        BigInteger M = new BigInteger(plaintext.getBytes());
        if (M.compareTo(n) >= 0) {
            throw new IllegalArgumentException("Message too large for modulus");
        }
        BigInteger C = M.modPow(e, n);
        return Base64.getEncoder().encodeToString(C.toByteArray());
    }
}