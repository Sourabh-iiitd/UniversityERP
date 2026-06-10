package edu.univ.erp.service;


import edu.univ.erp.domain.User;
import edu.univ.erp.util.DBConnection;
import com.password4j.Password;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Basically, Our Authentication Service
public class AuthService {

    public User login(String userName, String rawPasswd){
        String sqlStmt = "SELECT userID, userName, role, passwdHash, status FROM users WHERE userName= ?";

        try{
            Connection conn = DBConnection.getAuthConnection();
            PreparedStatement preparedStmt = conn.prepareStatement(sqlStmt);

            preparedStmt.setString(1, userName);
            ResultSet rs = preparedStmt.executeQuery();

            if(rs.next()){
                String storedHash = rs.getString("passwdHash");
                String status = rs.getString("status");
                String role = rs.getString("role");
                int userID = rs.getInt("userID");

                if("INACTIVE".equalsIgnoreCase(status)){
                    return null;
                }

                if(Password.check(rawPasswd, storedHash).withBcrypt()){
                    return new User(userID, userName, role, status);
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    return null;
    }

    // Testing Out Our Code
    public static void main(String[] args){
        AuthService auth = new AuthService();
        String newHash = Password.hash("password").withBcrypt().getResult();
        System.out.println(newHash);

        User admin = auth.login("admin", "password");
        if(admin != null){
            System.out.println("SUCCESS");
        }else{
            System.out.println("FAILURE");

        }
    }
}
