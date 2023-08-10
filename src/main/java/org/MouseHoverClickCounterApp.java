package org;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class MouseHoverClickCounterApp extends JFrame {

    private Map<Integer, JButton> buttonMap;
    private Map<String, Integer> clickCounts;

    public MouseHoverClickCounterApp() {
        clickCounts = new HashMap<>();
        buttonMap = new HashMap<>();
        initUI();
        initKeyBindings();
    }

    private void initUI() {
        setTitle("Mouse Hover Click Counter App");
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
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    int keyCode = e.getKeyCode();
                    if (keyCode >= KeyEvent.VK_1 && keyCode <= KeyEvent.VK_6) {
                        JButton button = buttonMap.get(keyCode - KeyEvent.VK_0);
                        if (button != null) {
                            button.doClick();
                            return true; // Consume the event to prevent further processing
                        }
                    }
                }
                return false;
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MouseHoverClickCounterApp app = new MouseHoverClickCounterApp();
            app.setVisible(true);
        });
    }
}
