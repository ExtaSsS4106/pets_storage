package com.example.petsstorage;

public class UserData {
    private String token;
    private String fullName;
    private String email;
    private String position;

    public UserData(String token, String fullName, String email, String position) {
        this.token = token;
        this.fullName = fullName;
        this.email = email;
        this.position = position;
    }

    public String getToken() { return token; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPosition() { return position; }
}
