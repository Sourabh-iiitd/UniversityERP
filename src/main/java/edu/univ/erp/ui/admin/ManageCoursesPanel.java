package edu.univ.erp.ui.admin;

import edu.univ.erp.data.Course;
import edu.univ.erp.service.AdminService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageCoursesPanel extends JPanel {

    private final AdminService service;
    private final JTextField codeField, nameField, creditsField;
    private final JTable coursesTable;
    private final DefaultTableModel tableModel;
    private Integer editingCourseId=null; //flag editing orr creating

    public ManageCoursesPanel() {
        this.service=new AdminService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //course form
        JPanel formPanel=new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Course Details"));
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.insets=new Insets(5, 5, 5, 5);
        gbc.fill=GridBagConstraints.HORIZONTAL;

        gbc.gridx=0; gbc.gridy=0; formPanel.add(new JLabel("Code (e.g. CS101):"), gbc);
        gbc.gridx=1; codeField=new JTextField(15); formPanel.add(codeField, gbc);

        gbc.gridx=0; gbc.gridy=1; formPanel.add(new JLabel("Course Name:"), gbc);
        gbc.gridx=1; nameField=new JTextField(15); formPanel.add(nameField, gbc);

        gbc.gridx=0; gbc.gridy=2; formPanel.add(new JLabel("Credits:"), gbc);
        gbc.gridx=1; creditsField=new JTextField(15); formPanel.add(creditsField, gbc);

        JButton saveButton=new JButton("Save Course");
        saveButton.addActionListener(e -> saveCourse());
        JButton clearButton=new JButton("Clear Form");
        clearButton.addActionListener(e -> clearForm());

        JPanel buttonPanel=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        gbc.gridx=1; gbc.gridy=3; formPanel.add(buttonPanel, gbc);

        //course table
        String[] columnNames={"ID", "Code", "Name", "Credits"};
        tableModel=new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        coursesTable=new JTable(tableModel);
        JScrollPane scrollPane=new JScrollPane(coursesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Existing Courses"));

        //button
        JPanel actionPanel=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton=new JButton("Edit Selected");
        editButton.addActionListener(e -> loadSelectedCourseForEditing());
        
        //delete btn
        JButton deleteButton=new JButton("Delete Selected");
        deleteButton.addActionListener(e -> deleteSelectedCourse());

        actionPanel.add(editButton);
        actionPanel.add(deleteButton);

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        refreshCoursesTable();
    }

    private void refreshCoursesTable() {
        tableModel.setRowCount(0);
        List<Course> courses=service.getAllCourses();
        for (Course course : courses) {
            tableModel.addRow(new Object[]{
                    course.getCourseId(),
                    course.getCourseCode(),
                    course.getCourseName(),
                    course.getCredits()
            });
        }
    }

    private void loadSelectedCourseForEditing() {
        int selectedRow=coursesTable.getSelectedRow();
        if (selectedRow==-1) {
            JOptionPane.showMessageDialog(this, "Please select a course from the table to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        editingCourseId=(Integer) tableModel.getValueAt(selectedRow, 0);
        codeField.setText((String) tableModel.getValueAt(selectedRow, 1));
        nameField.setText((String) tableModel.getValueAt(selectedRow, 2));
        creditsField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 3)));
    }
    private void deleteSelectedCourse() {
        int selectedRow=coursesTable.getSelectedRow();
        if (selectedRow==-1) {
            JOptionPane.showMessageDialog(this, "Please select a course from the table to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int courseId=(Integer) tableModel.getValueAt(selectedRow, 0);
        String courseName=(String) tableModel.getValueAt(selectedRow, 2);

       
        if (service.courseHasSections(courseId)) {
            JOptionPane.showMessageDialog(this, "Cannot delete '" + courseName + "'. It still has active sections.\nPlease delete all sections for this course first.", "Deletion Blocked", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int choice=JOptionPane.showConfirmDialog(this,
                "Are you sure you want to permanently delete the course '" + courseName + "'?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice==JOptionPane.YES_OPTION) {
            boolean success=service.deleteCourse(courseId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Course deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshCoursesTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete the course.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        editingCourseId=null;
        codeField.setText("");
        nameField.setText("");
        creditsField.setText("");
    }

    private void saveCourse() {
        try {
            String code=codeField.getText();
            String name=nameField.getText();
            int credits=Integer.parseInt(creditsField.getText());

            if (code.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Course Code and Name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success;
            String message;

            if (editingCourseId==null) {
                //newCourse
                success=service.addCourse(code, name, credits);
                message="Course Added Successfully!";
            } else {
                //updating
                success=service.updateCourse(editingCourseId, code, name, credits);
                message="Course Updated Successfully!";
            }

            if(success) {
                JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                refreshCoursesTable();
            } else {
                JOptionPane.showMessageDialog(this, "Operation failed. The course code might already exist.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch(NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input for Credits. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}