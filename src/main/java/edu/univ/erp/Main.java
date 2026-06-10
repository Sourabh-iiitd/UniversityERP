package edu.univ.erp;

import com.formdev.flatlaf.FlatLightLaf;
import edu.univ.erp.ui.LoginScreen;
import javax.swing.*;
import edu.univ.erp.service.StudentService;

public class Main {
    public static void main(String[] args) {

        // FlatLaf Setup
        try{
            FlatLightLaf.setup();
        }
        catch (Exception e){
            System.out.println("Couldn't Setup FlatLaf");
        }
        System.out.println(new StudentService().getAvailableSections());

        // Launch the UI
        SwingUtilities.invokeLater(() -> {
           LoginScreen login = new LoginScreen();
           login.setVisible(true);
        });
    }
} 