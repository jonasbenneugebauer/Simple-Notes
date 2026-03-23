import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
    private boolean isLoading = false;
    private JComboBox<String> categoryBox;
    private JComboBox<String> filterBox;
    private String[] categories = {"Alle", "Allgemein", "Arbeit", "Privat", "Uni", "Ideen"};
    private JTextField searchField;

    public NoteGUI() {
        this.field = new JTextField();
        this.area = new JTextArea();
        this.searchField = new JTextField();
        JScrollPane scrollPane = new JScrollPane(area);
        this.addButton = new JButton("Add");
        this.deleteButton = new JButton("Delete");
        this.frame = new JFrame("Simple Notes");
        this.newButton = new JButton("New");
        this.listModel = new DefaultListModel<>();
        this.list = new JList<>(listModel);
        this.categoryBox = new JComboBox<>(new String[]{"Allgemein", "Arbeit", "Privat", "Uni", "Ideen"});
        this.filterBox = new JComboBox<>(categories);

        frame.setLayout(new BorderLayout());
        frame.setSize(1080, 720);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // --- Rechtes Panel ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(newButton);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Runde Buttons & Farben
        addButton.putClientProperty("JButton.buttonType", "roundRect");
        deleteButton.putClientProperty("JButton.buttonType", "roundRect");
        newButton.putClientProperty("JButton.buttonType", "roundRect");
        addButton.setBackground(new Color(70, 130, 80));
        addButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(150, 50, 50));
        deleteButton.setForeground(Color.WHITE);
        newButton.setBackground(new Color(60, 100, 160));
        newButton.setForeground(Color.WHITE);

        // Titel + Kategorie oben rechts
        JLabel titleLabel = new JLabel("Title:");
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(field, BorderLayout.CENTER);
        titlePanel.add(categoryBox, BorderLayout.SOUTH); // Kategorie-Dropdown
        rightPanel.add(titlePanel, BorderLayout.NORTH);

        // --- Linkes Panel ---
        searchField.putClientProperty("JTextField.placeholderText", "Suche...");

        JPanel leftTopPanel = new JPanel(new BorderLayout());
        leftTopPanel.add(filterBox, BorderLayout.NORTH);   // Filter oben
        leftTopPanel.add(searchField, BorderLayout.SOUTH); // Suche darunter

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(leftTopPanel, BorderLayout.NORTH);
        leftPanel.add(list, BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(300, 0));

        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(rightPanel, BorderLayout.CENTER);
        frame.setVisible(true);

        // --- Daten laden ---
        noteManager.loadNotes();
        for (Note note : noteManager.getNotes()) {
            listModel.addElement(note.getTitle());
        }

        // --- Listener ---
        addButton.addActionListener(e -> {
            String selectedTitle = (String) list.getSelectedValue();
            if (selectedTitle != null) {
                for (int i = 0; i < noteManager.getNotes().size(); i++) {
                    if (noteManager.getNote(i).getTitle().equals(selectedTitle)) {
                        noteManager.editNote(i, field.getText(), area.getText(), (String) categoryBox.getSelectedItem());
                        listModel.set(list.getSelectedIndex(), field.getText());
                        break;
                    }
                }
            } else {
                addNote();
            }
        });

        deleteButton.addActionListener(e -> deleteNote());

        newButton.addActionListener(e -> {
            list.clearSelection();
            field.setText("");
            area.setText("");
            categoryBox.setSelectedIndex(0);
        });

        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedTitle = (String) list.getSelectedValue();
                if (selectedTitle != null) {
                    isLoading = true;
                    for (int i = 0; i < noteManager.getNotes().size(); i++) {
                        if (noteManager.getNote(i).getTitle().equals(selectedTitle)) {
                            field.setText(noteManager.getNote(i).getTitle());
                            area.setText(noteManager.getNote(i).getContent());
                            categoryBox.setSelectedItem(noteManager.getNote(i).getCategory()); // Kategorie laden
                            break;
                        }
                    }
                    isLoading = false;
                    addButton.setEnabled(false);
                } else {
                    addButton.setEnabled(true);
                }
            }
        });

        filterBox.addActionListener(e -> filterNotes(searchField.getText()));

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterNotes(searchField.getText()); }
            public void removeUpdate(DocumentEvent e) { filterNotes(searchField.getText()); }
            public void changedUpdate(DocumentEvent e) {}
        });

        DocumentListener autoSave = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { saveCurrentNote(); }
            public void removeUpdate(DocumentEvent e) { saveCurrentNote(); }
            public void changedUpdate(DocumentEvent e) {}
        };
        area.getDocument().addDocumentListener(autoSave);
        field.getDocument().addDocumentListener(autoSave);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                noteManager.saveNotes();
                System.exit(0);
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
        if (title.isEmpty() || content.isEmpty()) return;

        Note note = new Note(LocalDateTime.now(), title, content, (String) categoryBox.getSelectedItem());
        noteManager.addNote(note);
        listModel.addElement(note.getTitle());
        field.setText("");
        area.setText("");
        categoryBox.setSelectedIndex(0);
    }

    public void deleteNote() {
        String selectedTitle = (String) list.getSelectedValue();
        if (selectedTitle != null) {
            for (int i = 0; i < noteManager.getNotes().size(); i++) {
                if (noteManager.getNote(i).getTitle().equals(selectedTitle)) {
                    noteManager.deleteNote(i);
                    listModel.remove(list.getSelectedIndex());
                    break;
                }
            }
        }
    }

    public void filterNotes(String query) {
        String selectedCategory = (String) filterBox.getSelectedItem();
        listModel.clear();
        for (Note note : noteManager.getNotes()) {
            boolean matchesSearch = note.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    note.getContent().toLowerCase().contains(query.toLowerCase());
            boolean matchesCategory = selectedCategory.equals("Alle") ||
                    note.getCategory().equals(selectedCategory);
            if (matchesSearch && matchesCategory) {
                listModel.addElement(note.getTitle());
            }
        }
    }

    public void saveCurrentNote() {
        if (isLoading) return;
        String selectedTitle = (String) list.getSelectedValue();
        if (selectedTitle == null) return;

        for (int i = 0; i < noteManager.getNotes().size(); i++) {
            if (noteManager.getNote(i).getTitle().equals(selectedTitle)) {
                noteManager.editNote(i, field.getText(), area.getText(), (String) categoryBox.getSelectedItem());
                listModel.set(list.getSelectedIndex(), field.getText());
                break;
            }
        }
    }
}