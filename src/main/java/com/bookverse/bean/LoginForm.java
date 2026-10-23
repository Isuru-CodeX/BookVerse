package com.bookverse.bean;

import java.io.Serializable;

/** JavaBean backing the sign-in form. */
public class LoginForm implements Serializable {
    private static final long serialVersionUID = 1L;
    private String email = "";
    private String password = "";
    private String redirect = "";

    public LoginForm() { }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null ? "" : email.trim(); }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password == null ? "" : password; }
    public String getRedirect() { return redirect; }
    public void setRedirect(String redirect) { this.redirect = redirect == null ? "" : redirect.trim(); }
}
