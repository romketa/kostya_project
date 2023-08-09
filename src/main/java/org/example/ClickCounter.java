package org.example;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ClickCounter extends JFrame {

  private JButton[] buttons;
  private JLabel[] labels;
  private int[] clickCounts;

  public ClickCounter() {
    buttons = new JButton[6];
    labels = new JLabel[6];
    clickCounts = new int[6];

    // Создание главной панели и установка менеджера компоновки
    JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
    getContentPane().add(panel);

    // Создание кнопок, меток и установка обработчиков клика
    for (int i = 0; i < 6; i++) {
      buttons[i] = new JButton("Кнопка " + (i + 1));
      buttons[i].addActionListener(new ButtonClickListener(i));

      labels[i] = new JLabel("Количество кликов: 0");

      panel.add(buttons[i]);
      panel.add(labels[i]);
    }

    // Настройка окна
    setTitle("Click Counter");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    pack();
    setLocationRelativeTo(null);
    setVisible(true);
  }

  // Обработчик клика для каждой кнопки
  private class ButtonClickListener implements ActionListener {
    private int index;

    public ButtonClickListener(int index) {
      this.index = index;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      clickCounts[index]++; // Увеличение счетчика кликов
      labels[index].setText("Количество кликов: " + clickCounts[index]);
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      new ClickCounter();
    });
  }
}