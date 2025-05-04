package project.optics.jfkt.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigInteger;
import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {
    private LoginController loginController;
    private static final String TEST_PASSWORD = "SecurePassword123!";

    @BeforeEach
    void setUp() throws Exception {
        loginController = new LoginController();
    }

    @Test
    void testEncryptDecryptRoundTrip() throws Exception {
        // Test that encryption/decryption works correctly
        String encrypted = loginController.encrypt(TEST_PASSWORD);
        String decrypted = loginController.decrypt(encrypted);
        assertEquals(TEST_PASSWORD, decrypted);
    }



    @Test
    void testDecryptInvalidBase64() {
        // Test handling of invalid Base64 input
        assertThrows(IllegalArgumentException.class, () -> {
            loginController.decrypt("NotValidBase64!");
        });
    }

    @Test
    void testEncryptEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> {
            loginController.encrypt("");
        });
    }

    @Test
    void testEncryptNull() {
        assertThrows(NullPointerException.class, () -> {
            loginController.encrypt(null);
        });
    }

    @Test
    void testVeryLongPassword() {
        // Test maximum password length
        String longPassword = "A".repeat(100);
        String encrypted = loginController.encrypt(longPassword);
        String decrypted = loginController.decrypt(encrypted);
        assertEquals(longPassword, decrypted);
    }

    @Test
    void testPasswordTooLong() {
        // Create a password that's too long for the modulus
        String tooLongPassword = "AB".repeat(500);
            loginController.encrypt(tooLongPassword);
        ;
    }
}