import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import java.time.LocalDateTime;
public class NoteGUI {
    
private JList list;
private JTextField field;
private JTextArea area;
private JButton addButton;
private JButton deleteButton;
private JButton editButton;
private JFrame frame;
private DefaultListModel<String> listModel;
private NoteManager noteManager = new NoteManager();


public NoteGUI() {
    this.field = new JTextField();
    this.area = new JTextArea();
    JScrollPane scrollPane = new JScrollPane(area);
    this.addButton = new JButton("Add");
    this.deleteButton = new JButton("Delete");
    this.editButton = new JButton("Edit");
    this.frame = new JFrame("Simple Notes");
    this.listModel = new DefaultListModel<>();
    this.list = new JList<>(listModel);

    frame.setLayout(new BorderLayout());

    frame.setSize(1080, 720);
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
  

    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new BorderLayout());
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

    noteManager.loadNotes();
    for(Note note : noteManager.getNotes()){
        listModel.addElement(note.getTitle());
    }

    addButton.addActionListener(e -> addNote());
    deleteButton.addActionListener(e -> deleteNote());
    editButton.addActionListener(e -> editNote());

    frame.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent e) {
            noteManager.saveNotes();
            System.exit(0);
        }
    });
    
}
public static void main(String[] args) {
    new NoteGUI();
}

public void addNote(){
    String title = field.getText();
    String content = area.getText();

    if(title.isEmpty() || content.isEmpty()) return;
        System.out.println("Title or content cannot be empty.");
        
    Note note = new Note(LocalDateTime.now(), title, content);
    noteManager.addNote(note);
    System.out.println("Note added: " + title);

    listModel.addElement(note.getTitle());
    field.setText("");
    area.setText("");
}

public void deleteNote(){
    int index = list.getSelectedIndex();
    if(index != -1){
        noteManager.deleteNote(index);
        listModel.remove(index);
        System.out.println("Note deleted: " + index);
    } else {
        System.out.println("No note selected.");
    }
}

public void editNote(){
    int index = list.getSelectedIndex();
    if(index == -1) return;

    String newTitle = field.getText();
    String newContent = area.getText();
    if(newTitle.isEmpty() || newContent.isEmpty()) {
        System.out.println("Title or content cannot be empty.");
        return;
    }
    noteManager.editNote(index, newTitle, newContent);
    listModel.set(index, newTitle);
    System.out.println("Note edited: " + index);
}

}