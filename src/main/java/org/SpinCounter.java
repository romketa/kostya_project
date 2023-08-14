package org;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class SpinCounter extends JFrame {

    private final JButton[] buttons;
    private final JButton[] newButtons;
    private final int buttonCount = 6;
    private final int newButtonCount = 4;
    private final Map<JButton, Integer> currentSessionClickCounts;
    private final Map<JButton, Integer> overallClickCounts;
    private final DefaultTableModel tableModel1;
    private final DefaultTableModel tableModel2;
    private final DefaultTableModel tableModel3;
    private JButton lastClickedButton = null;
    private boolean isMouseOver = false;
    private boolean hotkeysEnabled = true;

    public SpinCounter() {
        currentSessionClickCounts = new HashMap<>();
        overallClickCounts = new HashMap<>();
        buttons = new JButton[buttonCount];
        newButtons = new JButton[newButtonCount];
        tableModel1 = new DefaultTableModel(new Object[]{"Multiplier", "Current Session", "Overall"}, 0);
        tableModel2 = new DefaultTableModel(new Object[]{"", "Played", "Av. Multiplier", "All-in Luck"}, 0);
        tableModel3 = new DefaultTableModel(new Object[]{"Button", "Current Session", "Overall"}, 0);
        initUI();
        initKeyBindings();
    }

    private void initUI() {
        setTitle("SpinCounter 0.1.0");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel buttonPanel1 = new JPanel(new GridLayout(2, 3));
        createButton(buttonPanel1, 0, "x2", "#6B340B");
        createButton(buttonPanel1, 1, "x3", "#CF2BA7");
        createButton(buttonPanel1, 2, "x5", "#128192");
        createButton(buttonPanel1, 3, "x8", "#198194");
        createButton(buttonPanel1, 4, "x50", "#C2BD00");
        createButton(buttonPanel1, 5, "x1000", "#E61100");

        JPanel buttonPanel2 = new JPanel(new GridLayout(1, 4));
        createNewButton(buttonPanel2, 0, "-EV and lost");
        createNewButton(buttonPanel2, 1, "-EV and won");
        createNewButton(buttonPanel2, 2, "+EV and lost");
        createNewButton(buttonPanel2, 3, "+EV and won");

        JPanel mainButtonPanel = new JPanel();
        mainButtonPanel.setLayout(new BoxLayout(mainButtonPanel, BoxLayout.Y_AXIS));
        mainButtonPanel.add(buttonPanel1);
        mainButtonPanel.add(buttonPanel2);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(mainButtonPanel, BorderLayout.NORTH);

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

        JTable table3 = new JTable(tableModel3) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells uneditable
            }
        };
        JScrollPane tableScrollPane3 = new JScrollPane(table3);

        JPanel tablesPanel = new JPanel();
        tablesPanel.setLayout(new GridLayout(3, 1));
        tablesPanel.add(tableScrollPane1);
        tablesPanel.add(tableScrollPane2);
        tablesPanel.add(tableScrollPane3);

        mainPanel.add(tablesPanel, BorderLayout.CENTER);

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
                    updateTable3();
                }
            }
        });

        JButton endSessionButton = new JButton("End Session");
        endSessionButton.addActionListener(e -> {
            for (JButton button : buttons) {
                currentSessionClickCounts.put(button, 0);
            }
            for (JButton button : newButtons) {
                currentSessionClickCounts.put(button, 0);
            }
            updateTable1();
            updateTable2();
            updateTable3();
        });

        actionPanel.add(undoButton);
        actionPanel.add(endSessionButton);

        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        JCheckBox enableHotkeysCheckBox = new JCheckBox("Enable Hotkeys", hotkeysEnabled);
        enableHotkeysCheckBox.addActionListener(e -> {
            hotkeysEnabled = enableHotkeysCheckBox.isSelected();
            updateHotkeyBindings();
        });

        JPanel hotkeyPanel = new JPanel(new FlowLayout());
        hotkeyPanel.add(enableHotkeysCheckBox);

        mainPanel.add(hotkeyPanel, BorderLayout.WEST);

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

        add(mainPanel);

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
            updateTable3();
        });

        panel.add(buttons[index]);
    }

    private void createNewButton(JPanel panel, int index, String label) {
        newButtons[index] = new JButton(label);

        currentSessionClickCounts.put(newButtons[index], 0);
        overallClickCounts.put(newButtons[index], 0);

        newButtons[index].addActionListener(e -> {
            JButton button = newButtons[index];
            lastClickedButton = button;
            int currentSessionCount = currentSessionClickCounts.get(button) + 1;
            int overallCount = overallClickCounts.get(button) + 1;
            currentSessionClickCounts.put(button, currentSessionCount);
            overallClickCounts.put(button, overallCount);
            updateTable3();
        });

        panel.add(newButtons[index]);
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
        tableModel2.addRow(new Object[]{"Current Session", totalPlayedCurrentSession, String.format("%.3f", averageMultiplierCurrentSession), ""});
        tableModel2.addRow(new Object[]{"Overall", totalPlayedOverall, String.format("%.3f", averageMultiplierOverall), ""});
    }

    private void updateTable3() {
        tableModel3.setRowCount(0);
        for (JButton button : newButtons) {
            int currentSessionCount = currentSessionClickCounts.get(button);
            int overallCount = overallClickCounts.get(button);
            tableModel3.addRow(new Object[]{button.getText(), currentSessionCount, overallCount});
        }
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
