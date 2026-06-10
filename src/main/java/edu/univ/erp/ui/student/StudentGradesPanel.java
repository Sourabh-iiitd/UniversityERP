package edu.univ.erp.ui.student;
import com.formdev.flatlaf.FlatClientProperties;
import edu.univ.erp.data.CourseSectionStructure;
import edu.univ.erp.data.TranscriptStructure;
import edu.univ.erp.domain.User;
import edu.univ.erp.data.GradesStructure;
import edu.univ.erp.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;



public class StudentGradesPanel extends JPanel{
    private final User currentUser;
    private final StudentService studentService;
    private JTable table;
    private DefaultTableModel tableModel;

    private List<GradesStructure> currentList;

    public StudentGradesPanel(User user){
        this.currentUser=user;
        this.studentService = new StudentService();
        initComponents();
        loadData();
    }

    private void initComponents(){
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Top Bar
        JPanel top = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Grades");
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +2");

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> loadData());

        top.add(title,BorderLayout.WEST);
        top.add(refresh,BorderLayout.EAST);

        // Download Transcript Button
        JButton downloadBtn = new JButton("Download Transcript");
        downloadBtn.addActionListener(e -> downloadTranscript());
        top.add(downloadBtn,BorderLayout.CENTER);

        add(top,BorderLayout.NORTH);

        String[] columns = {
                "Section ID", "Course Code", "Course Name",
                "Instructor", "Component", "Score(Quiz-20%, Midsem-30%,Endsem-50%)", "Max Score"
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
            currentList = studentService.getStudentGrades(currentUser.getUserID());
            for(GradesStructure s: currentList){
                Object[] row = {
                        s.getSectionID(),
                        s.getCourseCode(),
                        s.getCourseName(),
                        s.getInstructor(),
                        s.getComponentName(),
                        s.getScore(),
                        s.getMaxScore()
                };
                tableModel.addRow(row);
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load grades: " + e.getMessage());
        }
    }

    private void downloadTranscript(){
        try{
            List<TranscriptStructure> rows = studentService.getTranscript(currentUser.getUserID());

            if(rows.isEmpty()){
                JOptionPane.showMessageDialog(this, "No transcript data found");
                return;
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save Transcript");
            chooser.setSelectedFile(new java.io.File("transcript.csv"));

            int choice = chooser.showSaveDialog(this);
            if(choice != JFileChooser.APPROVE_OPTION){
                return;
            }

            java.io.File file = chooser.getSelectedFile();

            try(java.io.FileWriter fileWriter = new java.io.FileWriter(file)){

                fileWriter.write("Course Code,Course Name,Instructor,Section ID,Component,Score,Max Score\n");
                for(TranscriptStructure row : rows){
                    fileWriter.write(String.format("%s,%s,%s,%d,%s,%.2f,%.2f\n",
                            row.getCourseCode(),
                            row.getCourseName(),
                            row.getInstructor(),
                            row.getSectionID(),
                            row.getComponentName(),
                            row.getScore(),
                            row.getMaxScore()));
                }
                JOptionPane.showMessageDialog(this, "Transcript saved!");
            }
            catch(Exception e){
                JOptionPane.showMessageDialog(this, "Failed to save transcript: " + e.getMessage());
            }

        }
        catch(Exception e){
            JOptionPane.showMessageDialog(this, "Failed to save transcript: " + e.getMessage());
        }
    }
}
