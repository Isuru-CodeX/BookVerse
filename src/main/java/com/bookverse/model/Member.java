package com.bookverse.model;

import java.io.Serializable;
import java.util.Date;

/** Transfer Object for a registered library user (member or staff). */
public class Member implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String ROLE_MEMBER = "MEMBER";
    public static final String ROLE_STAFF = "STAFF";

    private int id;
    private String name = "";
    private String email = "";
    private String passwordHash = "";
    private String role = ROLE_MEMBER;
    private String membershipType = "STANDARD";
    private Date joinDate = new Date();
    private boolean active = true;

    public Member() { }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getMembershipType() { return membershipType; }
    public void setMembershipType(String membershipType) { this.membershipType = membershipType; }
    public Date getJoinDate() { return joinDate; }
    public void setJoinDate(Date joinDate) { this.joinDate = joinDate; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    /** ${member.staff} */
    public boolean isStaff() { return ROLE_STAFF.equals(role); }
}
