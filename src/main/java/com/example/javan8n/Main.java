package com.example.javan8n;

import com.example.javan8n.client.WebhookClient;
import com.example.javan8n.controller.AppController;
import com.example.javan8n.ui.AppFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppController controller = new AppController(new WebhookClient());
            AppFrame frame = new AppFrame(controller);
            frame.setVisible(true);
        });
    }
}
