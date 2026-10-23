package com.bookverse.bean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/** JavaBean backing the registration form, including server-side validation. */
public class RegistrationForm implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final List<String> MEMBERSHIP_TYPES = Arrays.asList("STANDARD", "PREMIUM", "STUDENT");
    private static final Pattern NAME = Pattern.compile("^[\\p{L}][\\p{L} .'-]{1,59}$");
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public static boolean isValidName(String n) { return n != null && NAME.matcher(n).matches(); }

    private String name = "";
    private String email = "";
    private String password = "";
    private String confirmPassword = "";
    private String membershipType = "STANDARD";

    public RegistrationForm() { }

    public String getName() { return name; }
    public void setName(String name) { this.name = name == null ? "" : name.trim(); }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null ? "" : email.trim().toLowerCase(); }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password == null ? "" : password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String c) { this.confirmPassword = c == null ? "" : c; }
    public String getMembershipType() { return membershipType; }
    public void setMembershipType(String t) { this.membershipType = t == null ? "" : t.trim(); }

    /** @return map of field name to error message; empty when the form is valid. */
    public Map<String, String> validate() {
        Map<String, String> errors = new LinkedHashMap<>();
        if (!isValidName(name)) {
            errors.put("name", "Enter your full name (2-60 letters; spaces, dots, hyphens and apostrophes allowed).");
        }
        if (!EMAIL.matcher(email).matches()) {
            errors.put("email", "Enter a valid email address, for example name@example.com.");
        }
        if (password.length() < 6) {
            errors.put("password", "Use a password with at least 6 characters.");
        }
        if (!password.equals(confirmPassword)) {
            errors.put("confirmPassword", "The two passwords do not match.");
        }
        if (!MEMBERSHIP_TYPES.contains(membershipType)) {
            errors.put("membershipType", "Choose one of the listed membership types.");
        }
        return errors;
    }
}
