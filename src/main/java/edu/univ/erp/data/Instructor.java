package edu.univ.erp.data;

public class Instructor {
    private final int userId;
    private final String fullName;

    public Instructor(int userId, String fullName) {
        this.userId=userId;
        this.fullName=fullName;
    }

    public int getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

   
    @Override
    public String toString() {
        return fullName;
    }
}