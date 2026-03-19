import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.time.LocalDateTime;

public class NoteGUI {

    private JList list;
    private JTextField field;
    private JTextArea area;
    private JButton addButton;
    private JButton deleteButton;
    private JButton newButton;
    private JFrame frame;
    private DefaultListModel<String> listModel;
    private NoteManager noteManager = new NoteManager();

    public NoteGUI() {
        this.field = new JTextField();
        this.area = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(area);
        this.addButton = new JButton("Add");
        this.deleteButton = new JButton("Delete");
        this.frame = new JFrame("Simple Notes");
        this.newButton = new JButton("New");
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
        buttonPanel.add(newButton);

        // Runde Buttons
        addButton.putClientProperty("JButton.buttonType", "roundRect");
        deleteButton.putClientProperty("JButton.buttonType", "roundRect");
        newButton.putClientProperty("JButton.buttonType", "roundRect");

        // Farben
        addButton.setBackground(new Color(70, 130, 80)); // Grün
        addButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(150, 50, 50)); // Rot
        deleteButton.setForeground(Color.WHITE);
        newButton.setBackground(new Color(60, 100, 160)); // Blau
        newButton.setForeground(Color.WHITE);

        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        JLabel titleLabel = new JLabel("Title:");
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(field, BorderLayout.CENTER);

        rightPanel.add(titlePanel, BorderLayout.NORTH);

        JTextField searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Suche...");

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(searchField, BorderLayout.NORTH);
        leftPanel.add(list, BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(300, 0));

        frame.add(leftPanel, BorderLayout.WEST);

        frame.add(rightPanel, BorderLayout.CENTER);

        list.setPreferredSize(new Dimension(300, 0));

        frame.setVisible(true);

        noteManager.loadNotes();
        for (Note note : noteManager.getNotes()) {
            listModel.addElement(note.getTitle());
        }

        addButton.addActionListener(e -> {
            int index = list.getSelectedIndex();
            if (index != -1) {
                // Bestehende Note updaten
                noteManager.editNote(index, field.getText(), area.getText());
                listModel.set(index, field.getText());
            } else {
                // Neue Note
                addNote();
            }
        });

        deleteButton.addActionListener(e -> deleteNote());
        newButton.addActionListener(e -> {
            list.clearSelection();
            field.setText("");
            area.setText("");
        });

      list.addListSelectionListener(e -> {
    if (!e.getValueIsAdjusting()) {
        String selectedTitle = (String) list.getSelectedValue();
        if (selectedTitle != null) {
            // Richtige Note über Titel suchen statt Index
            for (int i = 0; i < noteManager.getNotes().size(); i++) {
                if (noteManager.getNote(i).getTitle().equals(selectedTitle)) {
                    field.setText(noteManager.getNote(i).getTitle());
                    area.setText(noteManager.getNote(i).getContent());
                    break;
                }
            }
            addButton.setEnabled(false);
        } else {
            addButton.setEnabled(true);
        }
    }
});

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                noteManager.saveNotes();
                System.exit(0);
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filterNotes(searchField.getText());
            }

            public void removeUpdate(DocumentEvent e) {
                filterNotes(searchField.getText());
            }

            public void changedUpdate(DocumentEvent e) {
            }
        });

    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.out.println("FlatLaf konnte nicht geladen werden: " + e.getMessage());
        }
        new NoteGUI();
    }

    public void addNote() {
        String title = field.getText();
        String content = area.getText();

        if (title.isEmpty() || content.isEmpty())
            return;
        System.out.println("Title or content cannot be empty.");

        Note note = new Note(LocalDateTime.now(), title, content);
        noteManager.addNote(note);
        System.out.println("Note added: " + title);

        listModel.addElement(note.getTitle());
        field.setText("");
        area.setText("");
    }

    public void deleteNote() {
        int index = list.getSelectedIndex();
        if (index != -1) {
            noteManager.deleteNote(index);
            listModel.remove(index);
            System.out.println("Note deleted: " + index);
        } else {
            System.out.println("No note selected.");
        }
    }

    public void filterNotes(String query) {
        listModel.clear();
        for (Note note : noteManager.getNotes()) {
            if (note.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    note.getContent().toLowerCase().contains(query.toLowerCase())) {
                listModel.addElement(note.getTitle());
            }
        }

    }

}