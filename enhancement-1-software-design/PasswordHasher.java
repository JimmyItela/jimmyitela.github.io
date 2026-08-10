package com.example.weighttracker.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Salted PBKDF2 password hashing. Replaces the plaintext password column
 * that the original prototype stored and compared directly.
 *
 * <p>Uses PBKDF2WithHmacSHA1 rather than the SHA256 variant because this
 * app's minSdk is 24 and PBKDF2WithHmacSHA256 is only guaranteed available
 * starting at API 26. Salts and hashes are encoded as hex rather than with
 * {@code android.util.Base64} so this class has no Android framework
 * dependency and can run under plain JUnit on the host JVM.
 */
public final class PasswordHasher {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int ITERATIONS = 100_000;
    private static final int KEY_LENGTH_BITS = 256;
    private static final int SALT_LENGTH_BYTES = 16;

    private PasswordHasher() {
    }

    /** Generates a new random salt, encoded as hex for storage. */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        new SecureRandom().nextBytes(salt);
        return toHex(salt);
    }

    /** Derives a PBKDF2 hash of {@code password} using the given hex-encoded salt, encoded as hex. */
    public static String hash(String password, String saltHex) {
        byte[] salt = fromHex(saltHex);
        return toHex(pbkdf2(password, salt));
    }

    /**
     * Recomputes the hash for {@code password} with the stored salt and compares it to
     * {@code expectedHashHex} in constant time, so response timing cannot be used to
     * probe for correct password prefixes.
     */
    public static boolean verify(String password, String saltHex, String expectedHashHex) {
        byte[] candidate = fromHex(hash(password, saltHex));
        byte[] expected = fromHex(expectedHashHex);
        return MessageDigest.isEqual(candidate, expected);
    }

    private static byte[] pbkdf2(String password, byte[] salt) {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("PBKDF2 is not available on this device", e);
        } finally {
            ((PBEKeySpec) spec).clearPassword();
            Arrays.fill(salt, (byte) 0);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16));
            sb.append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }

    private static byte[] fromHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int hi = Character.digit(hex.charAt(i * 2), 16);
            int lo = Character.digit(hex.charAt(i * 2 + 1), 16);
            bytes[i] = (byte) ((hi << 4) + lo);
        }
        return bytes;
    }
}
