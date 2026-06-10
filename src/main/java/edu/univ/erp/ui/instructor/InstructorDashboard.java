package edu.univ.erp.ui.instructor;

import edu.univ.erp.data.CourseSectionStructure;
import edu.univ.erp.domain.User;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.ui.LoginScreen;
// import edu.univ.erp.ui.common.UIColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InstructorDashboard extends JFrame {

    private final User currentUser;
    private final InstructorService service = new InstructorService();
    private final JTabbedPane tabbedPane = new JTabbedPane();

    public InstructorDashboard(User user) {
        this.currentUser = user;
        setTitle("Instructor Dashboard - Welcome " + currentUser.getUserName());
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
   
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUserName());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginScreen().setVisible(true);
            }
        });
        headerPanel.add(logoutBtn, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        add(headerPanel, BorderLayout.NORTH);

        JPanel mySectionsPanel = createMySectionsPanel();
        tabbedPane.addTab("My Sections", mySectionsPanel);
        add(tabbedPane, BorderLayout.CENTER);


         if (service.isMaintenanceEnabled()) {
            JPanel maintenanceBanner = new JPanel();
            maintenanceBanner.setBackground(new Color(220, 53, 69));
            maintenanceBanner.setBorder(BorderFactory.createEmptyBorder(10, 25, 15, 25));

            JLabel warningLabel = new JLabel(" Maintenance mode is ON - Grades cannot be saved.");
            warningLabel.setForeground(Color.WHITE);
            warningLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            maintenanceBanner.add(warningLabel);

            add(maintenanceBanner, BorderLayout.SOUTH);
        }
        
    }

    private JPanel createMySectionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table of assigned sections
        String[] columnNames = {"ID", "Course Code", "Course Name", "Schedule", "Enrolled"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable sectionsTable = new JTable(tableModel);
        sectionsTable.removeColumn(sectionsTable.getColumnModel().getColumn(0)); 

        List<CourseSectionStructure> sections = service.getAssignedSections(currentUser.getUserID());
        for (CourseSectionStructure section : sections) {
            tableModel.addRow(new Object[]{
                    section.getSectionID(),
                    section.getCourseCode(),
                    section.getCourseName(),
                    section.getSchedule(),
                    section.getCapacity() + " / " + section.getEnrolled()
            });
        }

        panel.add(new JScrollPane(sectionsTable), BorderLayout.CENTER);

       
        JButton manageGradesBtn = new JButton("Manage Grades for Selected Section");
        manageGradesBtn.addActionListener(e -> {
            int selectedRow = sectionsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(panel, "Please select a section to manage.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int sectionId = (int) sectionsTable.getModel().getValueAt(selectedRow, 0);
            String courseName = (String) sectionsTable.getModel().getValueAt(selectedRow, 2);

            GradeManagementPanel gradePanel = new GradeManagementPanel(sectionId, courseName);
            tabbedPane.addTab("Grades: " + courseName, gradePanel);
            tabbedPane.setSelectedComponent(gradePanel);
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(manageGradesBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }
}