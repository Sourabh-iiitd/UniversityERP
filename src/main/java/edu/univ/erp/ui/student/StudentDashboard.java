package edu.univ.erp.ui.student;

import com.formdev.flatlaf.FlatClientProperties;
import edu.univ.erp.domain.User;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.ui.LoginScreen;
import edu.univ.erp.ui.common.UIColors;
import javax.swing.*;
import java.awt.*;


public class StudentDashboard extends JFrame {

    private final User currentUser;
    private final StudentService studentService;
    private JPanel maintenanceBanner;
    
    public StudentDashboard(User user) {
        this.currentUser = user;
        this.studentService = new StudentService();
        initComponents();
        updateMaintenanceBanner();
    }
    private void initComponents(){
        setTitle("Welcome " + currentUser.getUserName());
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Using borderLayout here as that will be better cuz this window will be resized
        setLayout(new BorderLayout());

        // top header
        JPanel headerPanel = new JPanel( new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15,25,15,25));

        // left, welcome text
        String name=currentUser.getUserName();
        String capitalizedName=name.substring(0, 1).toUpperCase() + name.substring(1);
        JLabel welcomeLabel=new JLabel("Welcome " + capitalizedName);
        welcomeLabel.putClientProperty(FlatClientProperties.STYLE, "font: bold +5");
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        // right, logout button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        logoutBtn.setFocusable(false);
        logoutBtn.addActionListener(e -> {
            logout();
        });
        headerPanel.add(logoutBtn, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

//        Maintenannce Banner Code
        maintenanceBanner = new JPanel();
        maintenanceBanner.setBackground(new Color(220, 53, 69));
        maintenanceBanner.setBorder(BorderFactory.createEmptyBorder(10,25,15,25));

        JLabel warningLabel = new JLabel("Maintenance mode is ON - Students cannot add / drop courses.");
        warningLabel.setForeground(Color.WHITE);
        warningLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        maintenanceBanner.add(warningLabel);

        add(maintenanceBanner, BorderLayout.SOUTH);
        // maintenanceBanner.setVisible(false);


        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty(FlatClientProperties.STYLE, "tabType: card; tabHeight: 35");
        tabbedPane.addTab("Course Catalog", new CourseCatalogPanel(currentUser));
        tabbedPane.addTab("My Registrations", new StudentRegistrationsPanel(currentUser));
        tabbedPane.addTab("My Grades", new StudentGradesPanel(currentUser));
        tabbedPane.addTab("TimeTable", new StudentTImeTablePanel(currentUser));


        add(tabbedPane, BorderLayout.CENTER);

    }

    private JPanel createPlaceholderTab(String title){
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel label = new JLabel(title);
        panel.add(label);
        return panel;
    }



    private void logout(){
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout? ");
        if (choice == JOptionPane.YES_OPTION){
            dispose();
            new LoginScreen().setVisible(true);
        }
    }
  

    private void updateMaintenanceBanner(){
        StudentService studentService = new StudentService();
        System.out.println(studentService.isMaintenanceMode());
        maintenanceBanner.setVisible(studentService.isMaintenanceMode());
    }



}
