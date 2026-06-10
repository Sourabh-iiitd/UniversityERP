package edu.univ.erp.ui.admin;

import edu.univ.erp.data.Course;
import edu.univ.erp.data.CourseSectionStructure;
import edu.univ.erp.data.Instructor;
import edu.univ.erp.service.AdminService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageSectionsPanel extends JPanel {

    private final AdminService service;
    private JComboBox<Course> courseComboBox;
    private JComboBox<Instructor> instructorComboBox;
    private JTextField scheduleField, roomField, capacityField;
    private JTable sectionsTable;
    private DefaultTableModel tableModel;
    private Integer editingSectionId=null; //flag if edit

    public ManageSectionsPanel() {
        this.service=new AdminService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //section form
        JPanel formPanel=new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Section Details"));
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.insets=new Insets(5, 5, 5, 5);
        gbc.fill=GridBagConstraints.HORIZONTAL;

        gbc.gridx=0; gbc.gridy=0; formPanel.add(new JLabel("Course:"), gbc);
        gbc.gridx=1; courseComboBox=new JComboBox<>(); formPanel.add(courseComboBox, gbc);

        gbc.gridx=0; gbc.gridy=1; formPanel.add(new JLabel("Instructor:"), gbc);
        gbc.gridx=1; instructorComboBox=new JComboBox<>(); formPanel.add(instructorComboBox, gbc);

        gbc.gridx=0; gbc.gridy=2; formPanel.add(new JLabel("Schedule:"), gbc);
        gbc.gridx=1; scheduleField=new JTextField(20); formPanel.add(scheduleField, gbc);

        gbc.gridx=0; gbc.gridy=3; formPanel.add(new JLabel("Room:"), gbc);
        gbc.gridx=1; roomField=new JTextField(20); formPanel.add(roomField, gbc);

        gbc.gridx=0; gbc.gridy=4; formPanel.add(new JLabel("Capacity:"), gbc);
        gbc.gridx=1; capacityField=new JTextField(20); formPanel.add(capacityField, gbc);

        JPanel buttonPanel=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton=new JButton("Save Section");
        saveButton.addActionListener(e -> saveSection());
        JButton clearButton=new JButton("Clear Form");
        clearButton.addActionListener(e -> clearForm());
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        gbc.gridx=1; gbc.gridy=5; formPanel.add(buttonPanel, gbc);

        //section table
        String[] columnNames={"ID", "Course Code", "Instructor", "Schedule", "Room", "Enrolled/Capacity"};
        tableModel=new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        sectionsTable=new JTable(tableModel);
        JScrollPane scrollPane=new JScrollPane(sectionsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Existing Sections"));

        //buttons
        JPanel actionPanel=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton=new JButton("Edit Selected");
        editButton.addActionListener(e -> loadSelectedSectionForEditing());

        JButton deleteButton=new JButton("Delete Selected");
        deleteButton.addActionListener(e -> deleteSelectedSection());

        actionPanel.add(editButton);
        actionPanel.add(deleteButton);

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        loadDropdownData();
        refreshSectionsTable();
    }

    private void loadDropdownData() {
        courseComboBox.removeAllItems();
        instructorComboBox.removeAllItems();
        service.getAllCourses().forEach(courseComboBox::addItem);
        service.getAllInstructors().forEach(instructorComboBox::addItem);
    }

    private void refreshSectionsTable() {
        tableModel.setRowCount(0);
        List<CourseSectionStructure> sections=service.getAllSections();
        for (CourseSectionStructure section : sections) {
            String enrolled=section.getEnrolled() + " / " + section.getCapacity();
            tableModel.addRow(new Object[]{
                    section.getSectionID(),
                    section.getCourseCode(),
                    section.getInstructor(),
                    section.getSchedule(),
                    section.getRoom(),
                    enrolled
            });
        }
    }

    private void loadSelectedSectionForEditing() {
        int selectedRow=sectionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a section to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        
        editingSectionId=(Integer) tableModel.getValueAt(selectedRow, 0);
        String courseCode=(String) tableModel.getValueAt(selectedRow, 1);
        String instructorName=(String) tableModel.getValueAt(selectedRow, 2);
        String schedule=(String) tableModel.getValueAt(selectedRow, 3);
        String room=(String) tableModel.getValueAt(selectedRow, 4);
        String capacityStr=((String) tableModel.getValueAt(selectedRow, 5)).split(" / ")[1];
     
        for (int i=0; i < courseComboBox.getItemCount(); i++) {
            if (courseComboBox.getItemAt(i).getCourseCode().equals(courseCode)) {
                courseComboBox.setSelectedIndex(i);
                break;
            }
        }
        for (int i=0; i < instructorComboBox.getItemCount(); i++) {
            if (instructorComboBox.getItemAt(i).getFullName().equals(instructorName)) {
                instructorComboBox.setSelectedIndex(i);
                break;
            }
        }

        scheduleField.setText(schedule);
        roomField.setText(room);
        capacityField.setText(capacityStr);
    }
    private void deleteSelectedSection() {
        int selectedRow=sectionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a section from the table to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int sectionId=(Integer) tableModel.getValueAt(selectedRow, 0);
        String courseCode=(String) tableModel.getValueAt(selectedRow, 1);

        if (service.sectionHasEnrollments(sectionId)) {
            JOptionPane.showMessageDialog(this, "Cannot delete section for '" + courseCode + "'. Students are enrolled in it.", "Deletion Blocked", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int choice=JOptionPane.showConfirmDialog(this,
                "Are you sure you want to permanently delete the section for '" + courseCode + "'?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            boolean success=service.deleteSection(sectionId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Section deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshSectionsTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete the section.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void clearForm() {
        editingSectionId=null;
        courseComboBox.setSelectedIndex(0);
        instructorComboBox.setSelectedIndex(0);
        scheduleField.setText("");
        roomField.setText("");
        capacityField.setText("");
    }

    private void saveSection() {
        try {
            Course selectedCourse=(Course) courseComboBox.getSelectedItem();
            Instructor selectedInstructor=(Instructor) instructorComboBox.getSelectedItem();
            String schedule=scheduleField.getText();
            String room=roomField.getText();
            int capacity=Integer.parseInt(capacityField.getText());

            if (selectedCourse == null || selectedInstructor == null || schedule.isEmpty() || room.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled out.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success;
            String message;

            if (editingSectionId == null) {
                //new sec
                success=service.addSection(selectedCourse.getCourseId(), selectedInstructor.getUserId(), schedule, room, capacity);
                message="Section added successfully!";
            } else {
                //update sec
                success=service.updateSection(editingSectionId, selectedCourse.getCourseId(), selectedInstructor.getUserId(), schedule, room, capacity);
                message="Section updated successfully!";
            }

            if (success) {
                JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                refreshSectionsTable();
            } else {
                JOptionPane.showMessageDialog(this, "Operation failed. Please check console for errors.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid capacity. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}