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
    private final Map<JButton, Integer> currentSessionClickCounts;
    private final Map<JButton, Integer> overallClickCounts;
    private final DefaultTableModel tableModel1;
    private final DefaultTableModel tableModel2;
    private JButton lastClickedButton = null;
    private boolean isMouseOver = false;
    private boolean hotkeysEnabled = true;

    public SpinCounter() {
        currentSessionClickCounts = new HashMap<>();
        overallClickCounts = new HashMap<>();
        buttons = new JButton[buttonCount];
        tableModel1 = new DefaultTableModel(new Object[]{"Multiplier", "Current Session", "Overall"}, 0);
        tableModel2 = new DefaultTableModel(new Object[]{"", "Played", "Av. Multiplier"}, 0);
        setAlwaysOnTop(true); // Set the program to always be on top
        requestFocus(); // Request focus on the program window

        initUI();
        initKeyBindings();
    }

    private void initUI() {
        setTitle("SpinCounter 0.0.9");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3));

        createButton(buttonPanel, 0, "x2", "#6B340B");
        createButton(buttonPanel, 1, "x3", "#CF2BA7");
        createButton(buttonPanel, 2, "x5", "#128192");
        createButton(buttonPanel, 3, "x8", "#198194");
        createButton(buttonPanel, 4, "x50", "#C2BD00");
        createButton(buttonPanel, 5, "x1000", "#E61100");

        JTable table1 = new JTable(tableModel1) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells uneditable
            }
        };
        JScrollPane tableScrollPane1 = new JScrollPane(table1);

        JTable table2 = new JTable(tableModel2) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells uneditable
            }
        };
        JScrollPane tableScrollPane2 = new JScrollPane(table2);

        JPanel tablesPanel = new JPanel();
        tablesPanel.setLayout(new GridLayout(2, 1));
        tablesPanel.add(tableScrollPane1);
        tablesPanel.add(tableScrollPane2);

        add(buttonPanel, BorderLayout.NORTH);
        add(tablesPanel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout());

        JButton undoButton = new JButton("Undo Action");
        undoButton.addActionListener(e -> {
            if (lastClickedButton != null) {
                int currentSessionCount = currentSessionClickCounts.get(lastClickedButton);
                int overallCount = overallClickCounts.get(lastClickedButton);
                if (currentSessionCount > 0) {
                    currentSessionClickCounts.put(lastClickedButton, currentSessionCount - 1);
                    overallClickCounts.put(lastClickedButton, overallCount - 1);
                    updateTable1();
                    updateTable2();
                }
            }
        });

        JButton endSessionButton = new JButton("End Session");
        endSessionButton.addActionListener(e -> {
            for (JButton button : buttons) {
                currentSessionClickCounts.put(button, 0);
            }
            updateTable1();
            updateTable2();
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

        currentSessionClickCounts.put(buttons[index], 0);
        overallClickCounts.put(buttons[index], 0);

        buttons[index].addActionListener(e -> {
            JButton button = buttons[index];
            lastClickedButton = button;
            int currentSessionCount = currentSessionClickCounts.get(button) + 1;
            int overallCount = overallClickCounts.get(button) + 1;
            currentSessionClickCounts.put(button, currentSessionCount);
            overallClickCounts.put(button, overallCount);
            updateTable1();
            updateTable2();
        });

        panel.add(buttons[index]);
    }

    private void initKeyBindings() {
        updateHotkeyBindings();
    }

    private void updateHotkeyBindings() {
        for (int i = 0; i < buttonCount; i++) {
            JButton button = buttons[i];

            String actionName = "ClickButton" + (i + 1);

            if (isMouseOver && hotkeysEnabled) {
                Action action = new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        button.doClick();
                    }
                };

                button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(getDefaultHotkey(i + 1), actionName);
                button.getActionMap().put(actionName, action);
            } else {
                button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(getDefaultHotkey(i + 1));
                button.getActionMap().remove(actionName);
            }
        }
    }

    private KeyStroke getDefaultHotkey(int number) {
        return KeyStroke.getKeyStroke(Character.forDigit(number, 10), 0);
    }

    private void updateTable1() {
        tableModel1.setRowCount(0);
        for (JButton button : buttons) {
            int currentSessionCount = currentSessionClickCounts.get(button);
            int overallCount = overallClickCounts.get(button);
            tableModel1.addRow(new Object[]{button.getText(), currentSessionCount, overallCount});
        }
    }

    private void updateTable2() {
        int totalPlayedCurrentSession = 0;
        int totalPlayedOverall = 0;
        int totalMultiplierCurrentSession = 0;
        int totalMultiplierOverall = 0;

        for (JButton button : buttons) {
            int currentSessionCount = currentSessionClickCounts.get(button);
            int overallCount = overallClickCounts.get(button);
            totalPlayedCurrentSession += currentSessionCount;
            totalPlayedOverall += overallCount;

            int multiplier = getMultiplier(button.getText());
            totalMultiplierCurrentSession += currentSessionCount * multiplier;
            totalMultiplierOverall += overallCount * multiplier;
        }

        double averageMultiplierCurrentSession = totalPlayedCurrentSession > 0 ? (double) totalMultiplierCurrentSession / totalPlayedCurrentSession : 0;
        double averageMultiplierOverall = totalPlayedOverall > 0 ? (double) totalMultiplierOverall / totalPlayedOverall : 0;

        tableModel2.setRowCount(0);
        tableModel2.addRow(new Object[]{"Current Session", totalPlayedCurrentSession, String.format("%.3f", averageMultiplierCurrentSession)});
        tableModel2.addRow(new Object[]{"Overall", totalPlayedOverall, String.format("%.3f", averageMultiplierOverall)});
    }

    private int getMultiplier(String buttonLabel) {
        switch (buttonLabel) {
            case "x2":
                return 2;
            case "x3":
                return 3;
            case "x5":
                return 5;
            case "x8":
                return 8;
            case "x50":
                return 50;
            case "x1000":
                return 1000;
            default:
                return 0;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SpinCounter app = new SpinCounter();
            app.setVisible(true);
        });
    }
}
