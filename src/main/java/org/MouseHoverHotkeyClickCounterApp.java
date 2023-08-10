package org;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class MouseHoverHotkeyClickCounterApp extends JFrame {

    private final JButton[] buttons;
    private final int buttonCount = 6;
    private final Map<JButton, Integer> clickCounts;
    private final DefaultTableModel tableModel;
    private boolean hotkeysEnabled = true;
    private final Map<JButton, KeyStroke> customHotkeys;

    public MouseHoverHotkeyClickCounterApp() {
        clickCounts = new HashMap<>();
        buttons = new JButton[buttonCount];
        tableModel = new DefaultTableModel(new Object[]{"Button", "Click Count"}, 0);
        customHotkeys = new HashMap<>();
        initUI();
        initKeyBindings();
    }

    private void initUI() {
        setTitle("Mouse Hover Hotkey Click Counter App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3));

        for (int i = 0; i < buttonCount; i++) {
            buttons[i] = new JButton("Button " + (i + 1));
            clickCounts.put(buttons[i], 0);

            int index = i;
            buttons[i].addActionListener(e -> {
                JButton button = buttons[index];
                int count = clickCounts.get(button) + 1;
                clickCounts.put(button, count);
                updateTable();
            });

            buttonPanel.add(buttons[i]);
        }

        add(buttonPanel, BorderLayout.SOUTH);

        JCheckBox enableHotkeysCheckBox = new JCheckBox("Enable Hotkeys", hotkeysEnabled);
        enableHotkeysCheckBox.addActionListener(e -> {
            hotkeysEnabled = enableHotkeysCheckBox.isSelected();
            updateHotkeyBindings();
        });

        JButton customizeHotkeysButton = new JButton("Customize Hotkeys");
        customizeHotkeysButton.addActionListener(e -> {
            showHotkeySettingsDialog();
        });

        JPanel settingsPanel = new JPanel(new FlowLayout());
        settingsPanel.add(enableHotkeysCheckBox);
        settingsPanel.add(customizeHotkeysButton);

        add(settingsPanel, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void initKeyBindings() {
        updateHotkeyBindings();
    }

    private void updateHotkeyBindings() {
        for (int i = 0; i < buttonCount; i++) {
            JButton button = buttons[i];

            KeyStroke keyStroke = customHotkeys.get(button);
            if (keyStroke == null) {
                keyStroke = getDefaultHotkey(i + 1);
            }

            Action action = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (hotkeysEnabled) {
                        button.doClick();
                    }
                }
            };

            String actionName = "ClickButton" + (i + 1);
            button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, actionName);
            button.getActionMap().put(actionName, action);
        }
    }

    private KeyStroke getDefaultHotkey(int number) {
        return KeyStroke.getKeyStroke(Character.forDigit(number, 10), 0);
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (JButton button : buttons) {
            int count = clickCounts.get(button);
            tableModel.addRow(new Object[]{button.getText(), count});
        }
    }

    private void showHotkeySettingsDialog() {
        JDialog dialog = new JDialog(this, "Customize Hotkeys", true);
        dialog.setLayout(new GridLayout(buttonCount + 1, 2));

        JLabel label = new JLabel("Click on a button and press a key combination:");
        dialog.add(label);

        for (int i = 0; i < buttonCount; i++) {
            JButton button = buttons[i];
            JLabel buttonLabel = new JLabel(button.getText());

            JTextField keyField = new JTextField();
            keyField.setText(getKeyStrokeText(customHotkeys.get(button)));

            keyField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    int keyCode = e.getKeyCode();
                    int modifiers = e.getModifiers();
                    String keyText = KeyEvent.getKeyModifiersText(modifiers) + " + " + KeyEvent.getKeyText(keyCode);
                    keyField.setText(keyText);
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    int keyCode = e.getKeyCode();
                    int modifiers = e.getModifiers();
                    KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, modifiers);

                    customHotkeys.put(button, keyStroke);
                    updateHotkeyBindings();
                    dialog.dispose();
                }
            });

            dialog.add(buttonLabel);
            dialog.add(keyField);
        }

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private String getKeyStrokeText(KeyStroke keyStroke) {
        if (keyStroke == null) {
            return "";
        }

        int modifiers = keyStroke.getModifiers();
        String modifiersText = KeyEvent.getKeyModifiersText(modifiers);
        String keyText = KeyEvent.getKeyText(keyStroke.getKeyCode());

        return modifiersText + " + " + keyText;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MouseHoverHotkeyClickCounterApp app = new MouseHoverHotkeyClickCounterApp();
            app.setVisible(true);
        });
    }
}
