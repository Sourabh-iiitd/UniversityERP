package edu.univ.erp.ui.admin;

import edu.univ.erp.domain.User;
import edu.univ.erp.ui.LoginScreen;
import edu.univ.erp.ui.common.UIColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JLabel titleLabel;

    public AdminDashboard(User user) {
        setTitle("University ERP - Admin Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //top panel
        JPanel topPanel=new JPanel(new BorderLayout());
        topPanel.setBackground(UIColors.PRIMARY_HEADER_BACKGROUND);
        topPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        titleLabel=new JLabel("Manage Users");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.WEST);

        JButton logoutButton=new JButton("Logout");
        logoutButton.addActionListener(e -> {
            int choice=JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginScreen().setVisible(true);
            }
        });
        topPanel.add(logoutButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        //side nav panel
        JPanel navPanel=new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(UIColors.SIDE_PANEL_BACKGROUND);
        navPanel.setPreferredSize(new Dimension(240, 0)); // wider to avoid truncation
        navPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));

        JLabel welcomeLabel=new JLabel("Welcome, " + user.getUserName());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setBorder(new EmptyBorder(20, 10, 20, 10));
        navPanel.add(welcomeLabel);

        navPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        //button
        navPanel.add(createNavButton("Manage Users", "USERS"));
        navPanel.add(createNavButton("Manage Courses", "COURSES"));
        navPanel.add(createNavButton("Manage Sections", "SECTIONS"));
        navPanel.add(createNavButton("System Settings", "SETTINGS"));

        add(navPanel, BorderLayout.WEST);

    //main panell
        cardLayout=new CardLayout();
        mainPanel=new JPanel(cardLayout);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(new ManageUsersPanel(), "USERS");
        mainPanel.add(new ManageCoursesPanel(), "COURSES");
        mainPanel.add(new ManageSectionsPanel(), "SECTIONS");
        mainPanel.add(new MaintenancePanel(), "SETTINGS");

        add(mainPanel, BorderLayout.CENTER);
    }

    private JButton createNavButton(String text, String cardName) {
        JButton button=new JButton(text);

        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));

        
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(UIColors.SIDE_PANEL_BACKGROUND);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.putClientProperty("html.disable", Boolean.TRUE);
        button.addActionListener(e -> {
            cardLayout.show(mainPanel, cardName);
            titleLabel.setText(text);
        });

        return button;
    }

}
