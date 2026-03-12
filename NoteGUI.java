import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Dimension;
public class NoteGUI {
    
private JList list;
private JTextField field;
private JTextArea area;
private JButton addButton;
private JButton deleteButton;
private JButton editButton;
private JFrame frame;



public NoteGUI() {
    this.list = new JList();
    this.field = new JTextField();
    this.area = new JTextArea();
    JScrollPane scrollPane = new JScrollPane(area);
    this.addButton = new JButton("Add");
    this.deleteButton = new JButton("Delete");
    this.editButton = new JButton("Edit");
    this.frame = new JFrame("Simple Notes");

    frame.setLayout(new BorderLayout());


    frame.setSize(1080, 720);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  

    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new BorderLayout());
    rightPanel.add(field, BorderLayout.NORTH);
    rightPanel.add(scrollPane, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(addButton);
    buttonPanel.add(deleteButton);
    buttonPanel.add(editButton);

    rightPanel.add(buttonPanel, BorderLayout.SOUTH);

    JLabel titleLabel = new JLabel("Title:");
    JPanel titlePanel = new JPanel(new BorderLayout());
    titlePanel.add(titleLabel, BorderLayout.NORTH);
    titlePanel.add(field, BorderLayout.CENTER);

    rightPanel.add(titlePanel, BorderLayout.NORTH);

    frame.add(list, BorderLayout.WEST);
    frame.add(rightPanel, BorderLayout.CENTER);

    list.setPreferredSize(new Dimension(300, 0));

    frame.setVisible(true);
}

public static void main(String[] args) {
    new NoteGUI();
}
}
