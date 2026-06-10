package edu.univ.erp.ui;

// we are using flatlaf
import com.formdev.flatlaf.FlatClientProperties;
import edu.univ.erp.domain.User;
import edu.univ.erp.service.AuthService;
import edu.univ.erp.ui.student.StudentDashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import edu.univ.erp.ui.admin.AdminDashboard;
import edu.univ.erp.ui.instructor.InstructorDashboard;
public class LoginScreen extends JFrame {

    private final AuthService authService;
    private JTextField userField;
    private JPasswordField passwdField;
    private JButton loginButton;


    private void initComponents(){
        setTitle("University ERP Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setSize(450, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        // This is our main panel implemented with Box Layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50)); // Creating Empty Borders For Padding

        // Title
        JLabel titleLabel = new JLabel("Welcome Back!");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        // SSome Spacing
        mainPanel.add(Box.createVerticalStrut(30));

        // username field
        userField = new JTextField();
        userField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Username");
        userField.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Height 40, Width Unlimited
        mainPanel.add(userField);

        mainPanel.add(Box.createVerticalStrut(15));

        passwdField = new JPasswordField();
        passwdField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");
        passwdField.putClientProperty(FlatClientProperties.STYLE, "arc: 10; showRevealButton: true");
        passwdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        mainPanel.add(passwdField);

        mainPanel.add(Box.createVerticalStrut(30));

        loginButton = new JButton("Login");
        loginButton.putClientProperty(FlatClientProperties.STYLE, "font: bold; arc: 10");
        loginButton.setBackground(new Color(0,120,215));
        loginButton.setForeground(Color.WHITE);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        loginButton.addActionListener(this::handleLogin);
        mainPanel.add(loginButton);

        getRootPane().setDefaultButton(loginButton);

        add(mainPanel);





    }


    private void handleLogin(ActionEvent e){

        String userName = userField.getText().trim();
        String passwd = new String(passwdField.getPassword());

        if(userName.isEmpty() || passwd.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter username and passwordd");
            return;
        }

        try{
            User user = authService.login(userName,passwd);
            if(user!= null){
                dispose();
                if("STUDENT".equalsIgnoreCase((user.getRole()))){
                    new StudentDashboard(user).setVisible(true);
                }else if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                    new AdminDashboard(user).setVisible(true);
                }else if ("INSTRUCTOR".equalsIgnoreCase(user.getRole())) {
                    new InstructorDashboard(user).setVisible(true);
                }else {
                    JOptionPane.showMessageDialog(this, "Unknown user role: " + user.getRole(), "Login Error", JOptionPane.ERROR_MESSAGE);
                    new LoginScreen().setVisible(true);
                }

            }else{
                JOptionPane.showMessageDialog(this, "Invalid username or password");
            }
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    public LoginScreen() {
        this.authService = new AuthService();
        initComponents();

    }


}
