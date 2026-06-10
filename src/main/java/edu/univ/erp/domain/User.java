package edu.univ.erp.domain;


public class User {
    private int userID;
    private String userName;
    private String role;
    private String status;



    public User(int userID, String userName, String role, String status){
        this.userID = userID;
        this.userName = userName;
        this.role = role;
        this.status = status;
    }

    // Some Getters FUnctions
    public int getUserID(){
        return userID;
    }
    public String getUserName(){
        return userName;
    }
    public String getRole(){
        return role;
    }
    public String getStatus(){
        return status;
    }


    @Override
    public String toString(){
        return "User{" + userName + "[" + role + "]}";
    }
}
