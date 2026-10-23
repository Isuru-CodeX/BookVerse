package com.bookverse.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Minimal salted SHA-256 hashing for the in-memory demo (use BCrypt/PBKDF2 in production). */
public final class PasswordUtil {
    // ⚡ Bolt: Pre-allocated hex array for faster byte-to-hex conversion
    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    private PasswordUtil() { }

    public static String hash(String email, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(("bookverse:" + email.toLowerCase() + ":" + password).getBytes(StandardCharsets.UTF_8));

            // ⚡ Bolt: Optimized hex encoding, replacing slow String.format("%02x")
            char[] hexChars = new char[d.length * 2];
            for (int j = 0; j < d.length; j++) {
                int v = d[j] & 0xFF;
                hexChars[j * 2] = HEX_ARRAY[v >>> 4];
                hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
            }
            return new String(hexChars);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
