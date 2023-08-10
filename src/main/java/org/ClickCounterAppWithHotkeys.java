package org;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class ClickCounterAppWithHotkeys extends JFrame {

    private Map<Integer, JButton> buttonMap;
    private Map<String, Integer> clickCounts;

    public ClickCounterAppWithHotkeys() {
        clickCounts = new HashMap<>();
        buttonMap = new HashMap<>();
        initUI();
        initKeyBindings();
    }

    private void initUI() {
        setTitle("Click Counter App with Hotkeys");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 3));

        String[] buttonLabels = {"Button 1", "Button 2", "Button 3", "Button 4", "Button 5", "Button 6"};

        for (int i = 0; i < buttonLabels.length; i++) {
            JButton button = new JButton(buttonLabels[i]);
            JLabel countLabel = new JLabel("0");
            buttonMap.put(i + 1, button);

            int finalI = i;
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int count = clickCounts.getOrDefault(buttonLabels[finalI], 0) + 1;
                    clickCounts.put(buttonLabels[finalI], count);
                    countLabel.setText(Integer.toString(count));
                }
            });

            add(button);
            add(countLabel);
        }

        pack();
        setLocationRelativeTo(null);
    }

    private void initKeyBindings() {
        for (int i = 1; i <= 6; i++) {
            int key = i + KeyEvent.VK_0;
            String buttonLabel = "Button " + i;
            JButton button = buttonMap.get(i);

            Action action = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    button.doClick();
                }
            };

            button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key, 0), buttonLabel);
            button.getActionMap().put(buttonLabel, action);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClickCounterAppWithHotkeys app = new ClickCounterAppWithHotkeys();
            app.setVisible(true);
        });
    }
}