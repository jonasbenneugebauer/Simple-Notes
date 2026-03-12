import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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
    this.addButton = new JButton("Add");
    this.deleteButton = new JButton("Delete");
    this.editButton = new JButton("Edit");
    this.frame = new JFrame("Simple Notes");
}
}
