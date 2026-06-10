package edu.univ.erp.ui.admin;

import edu.univ.erp.data.UserDetail;
import edu.univ.erp.service.AdminService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageUsersPanel extends JPanel {

    private final AdminService service;
    private final JTable usersTable;
    private final DefaultTableModel tableModel;

    public ManageUsersPanel() {
        this.service=new AdminService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //add userform
        JPanel formPanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New User"));
        JTextField usernameField=new JTextField(15);
        JPasswordField passwordField=new JPasswordField(15);
        JComboBox<String> roleComboBox=new JComboBox<>(new String[]{"STUDENT", "INSTRUCTOR"});
        JButton addUserButton=new JButton("Add User");
        addUserButton.addActionListener(e -> {
            String username=usernameField.getText();
            String password=new String(passwordField.getPassword());
            String role=(String) roleComboBox.getSelectedItem();
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean success=service.addUser(username, password, role);
            if (success) {
                JOptionPane.showMessageDialog(this, "User added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                usernameField.setText("");
                passwordField.setText("");
                refreshUsersTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Role:"));
        formPanel.add(roleComboBox);
        formPanel.add(addUserButton);

        //users table
        String[] columnNames={"ID", "Username", "Role", "Name", "Status"};
        tableModel=new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        usersTable=new JTable(tableModel);
        JScrollPane scrollPane=new JScrollPane(usersTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Existing Users"));

        //action buttons
        JPanel actionPanel=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deactivateButton=new JButton("Deactivate Selected");
        deactivateButton.addActionListener(e -> setUserStatus("INACTIVE"));
        JButton activateButton=new JButton("Activate Selected");
        activateButton.addActionListener(e -> setUserStatus("ACTIVE"));

        actionPanel.add(deactivateButton);
        actionPanel.add(activateButton);

        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        refreshUsersTable();
    }

    private void refreshUsersTable() {
        tableModel.setRowCount(0);
        List<UserDetail> users=service.getAllUserDetails();
        for (UserDetail user : users) {
            tableModel.addRow(new Object[]{
                    user.getUserId(),
                    user.getUsername(),
                    user.getRole(),
                    user.getName(),
                    user.getStatus()
            });
        }
    }

    private void setUserStatus(String status) {
        int selectedRow=usersTable.getSelectedRow();
        if (selectedRow==-1) {
            JOptionPane.showMessageDialog(this, "Please select a user from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId=(int) usersTable.getValueAt(selectedRow, 0);
        String username=(String) usersTable.getValueAt(selectedRow, 1);

        int choice=JOptionPane.showConfirmDialog(this,
                "Are you sure you want to set status to '" + status + "' for user '" + username + "'?",
                "Confirm Action", JOptionPane.YES_NO_OPTION);

        if (choice==JOptionPane.YES_OPTION) {
            boolean success=service.setUserStatus(userId, status);
            if (success) {
                JOptionPane.showMessageDialog(this, "User status updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshUsersTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update user status.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}