package edu.univ.erp.ui.student;
import com.formdev.flatlaf.FlatClientProperties;
import edu.univ.erp.domain.User;
import edu.univ.erp.data.CourseSectionStructure;
import edu.univ.erp.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentRegistrationsPanel extends JPanel {

    private final User currentUser;
    private final StudentService studentService;
    private JTable table;
    private DefaultTableModel tableModel;

    private List<CourseSectionStructure> currentList;

    public StudentRegistrationsPanel(User user){
        this.currentUser=user;
        this.studentService = new StudentService();
        initComponents();
        loadData();
    }

    private void initComponents(){
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        
        // Top
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JLabel title = new JLabel("My Registered Courses");
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +2");

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadData());

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(refreshBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {
                "Section ID", "Code", "Course Name",
                "Instructor", "Schedule", "Room", "Seats"
        };

        tableModel = new DefaultTableModel(columns, 0){
            @Override
            public boolean isCellEditable(int r, int c){
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "font: bold");

        add(new JScrollPane(table), BorderLayout.CENTER);

//         Bottom Buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton dropBtn = new JButton("Drop Course");
        dropBtn.addActionListener(e -> handleDrop());

        bottom.add(dropBtn);
        add(bottom, BorderLayout.SOUTH);

    }

    private void loadData(){
        try{
            tableModel.setRowCount(0);
            currentList = studentService.getStudentRegistrations(currentUser.getUserID());
            for(CourseSectionStructure s: currentList){
                Object[] row = {
                        s.getSectionID(),
                        s.getCourseCode(),
                        s.getCourseName(),
                        s.getInstructor(),
                        s.getSchedule(),
                        s.getRoom(),
                        s.getSeatsInfo()
                };
                tableModel.addRow(row);
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load catalog: " + e.getMessage());
        }
    }
    private void handleDrop(){
        int row = table.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(this, "Select a course / section first");
            return;
        }

        CourseSectionStructure selected = currentList.get(row);

        int confirm = JOptionPane.showConfirmDialog(this,"Confirm Drop " + selected.getCourseCode() + " , " + selected.getCourseName() + " ?");
        if(confirm == JOptionPane.YES_OPTION){
            try{
                studentService.dropSection(currentUser.getUserID(), selected.getSectionID());
                JOptionPane.showMessageDialog(this, "Successfully Dropped!");
                loadData();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Dropping Failed: " + e.getMessage());
            }
        }
    }

}
