package edu.univ.erp.ui.student;
import com.formdev.flatlaf.FlatClientProperties;
import edu.univ.erp.domain.User;
import edu.univ.erp.data.CourseSectionStructure;
import edu.univ.erp.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Moved this to new File because it was getting huge
public class CourseCatalogPanel extends JPanel {

    private final User currentUser;
    private final StudentService studentService;
    private JTable courseTable;
    private DefaultTableModel tableModel;

    private List<CourseSectionStructure> currentList;

    public CourseCatalogPanel(User currentUser){
        this.currentUser=currentUser;
        this.studentService=new StudentService();
        initComponents();
        loadData();
    }

    private void initComponents(){
        setLayout(new BorderLayout(10,10));

        // Title Bar
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Available Courses / Sections");
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +2");

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadData());

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(refreshBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Table Data
        String[] columns = {"ID", "Code", "Name", "Instructor", "Schedule", "Room", "Enrolled/Capacity", "Drop Deadline"};
        tableModel = new DefaultTableModel(columns, 0){
            @Override
            public boolean isCellEditable(int r, int c){
                return false;
            }
        };

        courseTable = new JTable(tableModel);
        courseTable.setRowHeight(30);
        courseTable.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "font: bold");

        JScrollPane scrollPane = new JScrollPane(courseTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton registerBtn = new JButton("Register for Selected");
        registerBtn.putClientProperty(FlatClientProperties.STYLE, "font: bold; arc: 10");
        registerBtn.setBackground(new Color(0,120,215));
        registerBtn.setForeground(Color.white);
        registerBtn.addActionListener(e -> {
            handleRegister();
        });

        bottomPanel.add(registerBtn);
        add(bottomPanel, BorderLayout.SOUTH);




    }



    // Loading Data from Backend
    private void loadData(){
        try{
            tableModel.setRowCount(0);
            currentList = studentService.getAvailableSections();

                    for(CourseSectionStructure s: currentList){
                        Object[] row = {
                                s.getSectionID(),
                                s.getCourseCode(),
                                s.getCourseName(),
                                s.getInstructor(),
                                s.getSchedule(),
                                s.getRoom(),
                                s.getSeatsInfo(),
                                s.getDropDeadline()
                        };
                        tableModel.addRow(row);
                    }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load catalog: " + e.getMessage());
        }
    }


    private void handleRegister(){
        int row = courseTable.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(this, "Select a course / section first");
            return;
        }

        CourseSectionStructure selected = currentList.get(row);

        int confirm = JOptionPane.showConfirmDialog(this,"Register For " + selected.getCourseCode() + " , " + selected.getCourseName() + " ?");
        if(confirm == JOptionPane.YES_OPTION){
            try{
                studentService.registerStudent(currentUser.getUserID(), selected.getSectionID());
                JOptionPane.showMessageDialog(this, "Successfully registered!");
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Registration Failed: " + e.getMessage());
            }
        }
    }
}
