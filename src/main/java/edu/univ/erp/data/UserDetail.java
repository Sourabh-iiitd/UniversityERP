package edu.univ.erp.data;

public class UserDetail {
    private final int userId;
    private final String username;
    private final String role;
    private final String name;
    private final String status;

    public UserDetail(int userId, String username, String role, String name, String status) {
        this.userId=userId;
        this.username=username;
        this.role=role;
        this.name=name;
        this.status=status;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getName() { return name; }
    public String getStatus() { return status; }
}