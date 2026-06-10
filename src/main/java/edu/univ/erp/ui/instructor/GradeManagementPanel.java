package edu.univ.erp.ui.instructor;

import edu.univ.erp.data.EnrollmentDetail;
import edu.univ.erp.data.Grade;
import edu.univ.erp.service.InstructorService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class GradeManagementPanel extends JPanel {

    private final InstructorService service = new InstructorService();
    private final DefaultTableModel tableModel;
    private final JTable gradesTable;
    private final int sectionId;

    public GradeManagementPanel(int sectionId, String courseName) {
        this.sectionId = sectionId;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Manage Grades for: " + courseName));

        String[] columnNames = {"Enrollment ID", "Student Name", "Quiz(20%)", "Midterm(30%)", "Final Exam(50%)", "Overall"};
     
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 5 && column != 1 && column != 0;
            }
        };
        
        gradesTable = new JTable(tableModel);

        gradesTable.removeColumn(gradesTable.getColumnModel().getColumn(0));

        add(new JScrollPane(gradesTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton saveButton = new JButton("Save All Grades");
        saveButton.addActionListener(e -> saveGrades(true));
        
      //compute btn
        JButton computeButton = new JButton("Compute Final Grades (20/30/50)");
        computeButton.addActionListener(e -> {
           
            if (saveGrades(false)) { 
          
                service.computeFinalGradesForSection(sectionId);
               
                loadStudentGrades(); 
                JOptionPane.showMessageDialog(this, "Final grades computed and saved as 'Overall'.");
            }
        });

        bottomPanel.add(saveButton);
        bottomPanel.add(computeButton);
        
        add(bottomPanel, BorderLayout.SOUTH);

        loadStudentGrades();
    }

    private void loadStudentGrades() {
        tableModel.setRowCount(0);
        List<EnrollmentDetail> students = service.getEnrolledStudents(sectionId);

        for (EnrollmentDetail student : students) {
            Vector<Object> row = new Vector<>();
            row.add(student.getEnrollmentId());
            row.add(student.getStudentName());

            List<Grade> grades = service.getGradesForEnrollment(student.getEnrollmentId());
            double quizScore = 0.0, midtermScore = 0.0, finalExamScore = 0.0, overallScore = 0.0;
            
            for (Grade grade : grades) {
                if (grade.getAssessment() != null) {
                    
                    String componentName = grade.getAssessment().trim();
                    
                    if (componentName.equalsIgnoreCase("Quiz")) {
                        quizScore = grade.getScore();
                    } else if (componentName.equalsIgnoreCase("Midterm")) {
                        midtermScore = grade.getScore();
                    } else if (componentName.equalsIgnoreCase("Final Exam")) {
                        finalExamScore = grade.getScore();
                    } else if (componentName.equalsIgnoreCase("Overall")) {
                        overallScore = grade.getScore();
                    }
                }
            }
            row.add(quizScore);
            row.add(midtermScore);
            row.add(finalExamScore);
            row.add(overallScore);
            tableModel.addRow(row);
        }
    }

            private boolean saveGrades(boolean showSuccessDialog) {
                if (service.isMaintenanceEnabled()) {
                    JOptionPane.showMessageDialog(this, "⚠ Maintenance Mode is ON. Grades cannot be saved.", "Blocked", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
        
                if (gradesTable.isEditing()) gradesTable.getCellEditor().stopCellEditing();
        
                boolean allSuccess = true;
        
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    try {
                        int enrollmentId = (int) tableModel.getValueAt(i, 0);
        
                      
                        double quiz = parseScore(tableModel.getValueAt(i, 2));     
                        double midterm = parseScore(tableModel.getValueAt(i, 3));   
                        double finalExam = parseScore(tableModel.getValueAt(i, 4));
        
             
                        boolean s1 = service.saveOrUpdateGrade(enrollmentId, "Quiz", quiz);
                        boolean s2 = service.saveOrUpdateGrade(enrollmentId, "Midterm", midterm);
                        boolean s3 = service.saveOrUpdateGrade(enrollmentId, "Final Exam", finalExam);
        
                        if (!s1 || !s2 || !s3) allSuccess = false;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        allSuccess = false;
                    }
                }
        
                if (allSuccess && showSuccessDialog) {
                    JOptionPane.showMessageDialog(this, "All grades saved successfully!");
                }
                return allSuccess;
            }
        

            private double parseScore(Object value) {
                if (value == null || value.toString().trim().isEmpty()) return 0.0;
                try {
                    return Double.parseDouble(value.toString());
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
}