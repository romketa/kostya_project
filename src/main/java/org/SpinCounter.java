package org;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class SpinCounter extends JFrame {

    private final JButton[] buttons;
    private final int buttonCount = 6;
    private final Map<JButton, Integer> clickCounts;
    private final DefaultTableModel tableModel;
    private boolean hotkeysEnabled = true;
    private final Map<JButton, KeyStroke> defaultHotkeys;
    private final Map<JButton, Integer> undoCounts;
    private JButton lastClickedButton = null;
    private boolean isMouseOver = false;

    public SpinCounter() {
        clickCounts = new HashMap<>();
        undoCounts = new HashMap<>();
        buttons = new JButton[buttonCount];
        tableModel = new DefaultTableModel(new Object[]{"Multiplier", "Games"}, 0);
        defaultHotkeys = new HashMap<>();
        initUI();
        initKeyBindings();
    }

    private void initUI() {
        setTitle("SpinCounter 0.0.4");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3));

        createButton(buttonPanel, 0, "x2", "#6B340B");
        createButton(buttonPanel, 1, "x3", "#CF2BA7");
        createButton(buttonPanel, 2, "x5", "#128192");
        createButton(buttonPanel, 3, "x8", "#198194");
        createButton(buttonPanel, 4, "x50", "#C2BD00");
        createButton(buttonPanel, 5, "x1000", "#E61100");

        JTable table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells uneditable
            }
        };
        JScrollPane tableScrollPane = new JScrollPane(table);

        add(buttonPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout());

        JButton undoButton = new JButton("Undo Action");
        undoButton.addActionListener(e -> {
            if (lastClickedButton != null) {
                int count = undoCounts.get(lastClickedButton);
                if (count > 0) {
                    clickCounts.put(lastClickedButton, count - 1);
                    undoCounts.put(lastClickedButton, count - 1);
                    updateTable();
                }
            }
        });

        JButton endSessionButton = new JButton("End Session");
        endSessionButton.addActionListener(e -> {
            for (JButton button : buttons) {
                clickCounts.put(button, 0);
                undoCounts.put(button, 0);
            }
            updateTable();
        });

        actionPanel.add(undoButton);
        actionPanel.add(endSessionButton);

        add(actionPanel, BorderLayout.SOUTH);

        JCheckBox enableHotkeysCheckBox = new JCheckBox("Enable Hotkeys", hotkeysEnabled);
        enableHotkeysCheckBox.addActionListener(e -> {
            hotkeysEnabled = enableHotkeysCheckBox.isSelected();
            updateHotkeyBindings();
        });

        JPanel hotkeyPanel = new JPanel(new FlowLayout());
        hotkeyPanel.add(enableHotkeysCheckBox);

        add(hotkeyPanel, BorderLayout.WEST);

        // Mouse listener to detect mouse over the program window
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isMouseOver = true;
                updateHotkeyBindings();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isMouseOver = false;
                updateHotkeyBindings();
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    private void createButton(JPanel panel, int index, String label, String color) {
        buttons[index] = new JButton(label);
        buttons[index].setBackground(Color.decode(color));
        buttons[index].setOpaque(true);
        buttons[index].setBorderPainted(false);

        clickCounts.put(buttons[index], 0);
        undoCounts.put(buttons[index], 0);

        buttons[index].addActionListener(e -> {
            JButton button = buttons[index];
            lastClickedButton = button;
            int count = clickCounts.get(button) + 1;
            clickCounts.put(button, count);
            undoCounts.put(button, count);
            updateTable();
        });

        panel.add(buttons[index]);
    }

    private void initKeyBindings() {
        defaultHotkeys.put(buttons[0], getDefaultHotkey(1)); // Default hotkey for Button 1
        updateHotkeyBindings();
    }

    private void updateHotkeyBindings() {
        for (int i = 0; i < buttonCount; i++) {
            JButton button = buttons[i];

            KeyStroke keyStroke = defaultHotkeys.get(button);
            if (keyStroke == null) {
                keyStroke = getDefaultHotkey(i + 1);
            }

            String actionName = "ClickButton" + (i + 1);

            if (isMouseOver && hotkeysEnabled) {
                Action action = new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        button.doClick();
                    }
                };

                button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, actionName);
                button.getActionMap().put(actionName, action);
            } else {
                button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(keyStroke);
                button.getActionMap().remove(actionName);
            }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SpinCounter app = new SpinCounter();
            app.setVisible(true);
        });
    }
}
