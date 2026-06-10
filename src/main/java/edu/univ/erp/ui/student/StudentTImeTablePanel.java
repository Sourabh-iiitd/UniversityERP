package edu.univ.erp.ui.student;
import com.formdev.flatlaf.FlatClientProperties;
import edu.univ.erp.data.CourseSectionStructure;
import edu.univ.erp.data.GradesStructure;
import edu.univ.erp.domain.User;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.ui.common.UIColors;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


public class StudentTImeTablePanel extends JPanel {

    private final User currentUser;
    private final StudentService studentService;
    private JTable table;
    private DefaultTableModel tableModel;

    private List<CourseSectionStructure> currentList;

    public StudentTImeTablePanel(User currentUser){
        this.currentUser=currentUser;
        this.studentService=new StudentService();
        initComponents();
        loadData();
    }

    private void initComponents(){
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Top Bar
        JPanel top = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Time Table");
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +2");

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> loadData());

        top.add(title,BorderLayout.WEST);
        top.add(refresh,BorderLayout.EAST);

        add(top,BorderLayout.NORTH);

        String[] columns = {
                "Section ID", "Course Code", "Course Name",
                "Instructor", "Schedule", "Room"
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
    }


    private void loadData(){
        try{
            tableModel.setRowCount(0);
            currentList = studentService.getStudentTimeTable(currentUser.getUserID());
            for(CourseSectionStructure s: currentList){
                Object[] row = {
                        s.getSectionID(),
                        s.getCourseCode(),
                        s.getCourseName(),
                        s.getInstructor(),
                        s.getSchedule(),
                        s.getRoom(),
                };
                tableModel.addRow(row);
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load timetable: " + e.getMessage());
        }
    }
}
