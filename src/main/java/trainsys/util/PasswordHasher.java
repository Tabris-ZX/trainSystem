package trainsys.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordHasher {
    private static final String PREFIX = "sha256$";

    private PasswordHasher() {
    }

    public static String hash(String rawPassword) {
        String normalized = rawPassword == null ? "" : rawPassword;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(normalized.getBytes(StandardCharsets.UTF_8));
            return PREFIX + toHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    public static boolean matches(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        if (isHashed(storedPassword)) {
            return storedPassword.equals(hash(rawPassword));
        }
        return storedPassword.equals(rawPassword == null ? "" : rawPassword);
    }

    public static boolean needsUpgrade(String storedPassword) {
        return storedPassword == null || !isHashed(storedPassword);
    }

    private static boolean isHashed(String storedPassword) {
        return storedPassword.startsWith(PREFIX);
    }

    private static String toHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte current : bytes) {
            builder.append(Character.forDigit((current >> 4) & 0xF, 16));
            builder.append(Character.forDigit(current & 0xF, 16));
        }
        return builder.toString();
    }
}
