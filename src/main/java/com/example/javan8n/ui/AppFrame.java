package com.example.javan8n.ui;

import com.example.javan8n.controller.AppController;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class AppFrame extends JFrame {
    private final JTextField webhookField = new JTextField(30);
    private final JTextField nameField = new JTextField(30);
    private final JTextField emailField = new JTextField(30);
    private final JTextArea queryArea = new JTextArea(6, 30);
    private final JButton submitButton = new JButton("Submit");

    private final AppController controller;

    public AppFrame(AppController controller) {
        this.controller = controller;
        setTitle("Java -> n8n Contact Form");
        setSize(560, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildFormPanel(), BorderLayout.CENTER);
        wireSubmit();
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        addLabeledInput(panel, gbc, 0, "n8n Webhook URL:", webhookField);
        addLabeledInput(panel, gbc, 1, "Name:", nameField);
        addLabeledInput(panel, gbc, 2, "Email:", emailField);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Query:"), gbc);

        gbc.gridx = 1;
        queryArea.setLineWrap(true);
        queryArea.setWrapStyleWord(true);
        panel.add(new JScrollPane(queryArea), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(submitButton, gbc);

        return panel;
    }

    private void addLabeledInput(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void wireSubmit() {
        submitButton.addActionListener(event -> {
            setFormEnabled(false);

            String webhookUrl = webhookField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String query = queryArea.getText().trim();

            new Thread(() -> {
                try {
                    String responseBody = controller.submit(webhookUrl, name, email, query);
                    SwingUtilities.invokeLater(() -> {
                        String message = responseBody.isEmpty()
                                ? "Submitted successfully. n8n will continue with Google Sheets and Gmail nodes."
                                : "Submitted successfully. n8n response: " + responseBody;
                        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                        clearFields();
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                            this,
                            "Submission failed: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    ));
                } finally {
                    SwingUtilities.invokeLater(() -> setFormEnabled(true));
                }
            }, "webhook-submit-thread").start();
        });
    }

    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        queryArea.setText("");
    }

    private void setFormEnabled(boolean enabled) {
        webhookField.setEnabled(enabled);
        nameField.setEnabled(enabled);
        emailField.setEnabled(enabled);
        queryArea.setEnabled(enabled);
        submitButton.setEnabled(enabled);
    }
}
