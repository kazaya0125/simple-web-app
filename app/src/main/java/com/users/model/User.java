package com.users.model;

public class User {

    private long id;
    private String organizationcode;
    private String username;
    private String accountType;
    private boolean enabled;

    public User(long id, String organizationcode, String username, String accountType, boolean enabled) {
        this.id = id;
        this.organizationcode = organizationcode;
        this.username = username;
        this.accountType = accountType;
        this.enabled = enabled;
    }
}