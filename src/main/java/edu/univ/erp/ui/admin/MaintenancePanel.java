package edu.univ.erp.ui.admin;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.UIColors;

import javax.swing.*;
import java.awt.*;

public class MaintenancePanel extends JPanel {

    private AdminService adminService;
    private JToggleButton maintenanceToggleButton;
    private JLabel statusLabel;

    public MaintenancePanel() {
        this.adminService=new AdminService();
        setBackground(Color.WHITE);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("System Settings"));

        GridBagConstraints gbc=new GridBagConstraints();
        gbc.insets=new Insets(10, 10, 10, 10);

        //status Display
        statusLabel=new JLabel();
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        //toggle btn
        maintenanceToggleButton=new JToggleButton("Toggle Maintenance Mode");
        maintenanceToggleButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        maintenanceToggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        maintenanceToggleButton.addActionListener(e -> toggleMaintenanceMode());

        gbc.gridy=0;
        add(new JLabel("Maintenance Mode is currently:"), gbc);
        gbc.gridy=1;
        add(statusLabel, gbc);
        gbc.gridy=2;
        add(maintenanceToggleButton, gbc);

        
        updateStatus();
    }

    private void updateStatus() {
        boolean isMaintenanceOn=adminService.isMaintenanceModeOn();
        maintenanceToggleButton.setSelected(isMaintenanceOn);
        if (isMaintenanceOn) {
            statusLabel.setText("ON");
            statusLabel.setForeground(UIColors.DANGER_COLOR);
            maintenanceToggleButton.setText("Turn OFF");
        } else {
            statusLabel.setText("OFF");
            statusLabel.setForeground(UIColors.SUCCESS_COLOR);
            maintenanceToggleButton.setText("Turn ON");
        }
    }

    private void toggleMaintenanceMode() {
        boolean currentState=adminService.isMaintenanceModeOn();
        adminService.setMaintenanceMode(!currentState);
        updateStatus(); //Refresh
        JOptionPane.showMessageDialog(this,
                "Maintenance mode is now " + (adminService.isMaintenanceModeOn() ? "ON" : "OFF"),
                "Status Updated",
                JOptionPane.INFORMATION_MESSAGE);
    }
}