import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import javax.swing.text.html.*;

import com.formdev.flatlaf.FlatDarkLaf;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NoteGUI {

    private JList<String> list;
    private JTextField field;
    private JTextPane textPane;
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
    private JLabel dateLabel;

    public NoteGUI() {
        this.field = new JTextField();
        this.textPane = new JTextPane();
        this.textPane.setContentType("text/html");
        this.searchField = new JTextField();
        this.dateLabel = new JLabel("");

        JScrollPane scrollPane = new JScrollPane(textPane);
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

        // --- Toolbar ---
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 60)));

        JButton boldBtn = new JButton("B");
        boldBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boldBtn.setToolTipText("Fett (Ctrl+B)");

        JButton italicBtn = new JButton("I");
        italicBtn.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        italicBtn.setToolTipText("Kursiv (Ctrl+I)");

        JButton h1Btn = new JButton("H1");
        h1Btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        h1Btn.setToolTipText("Überschrift");

        JButton listBtn = new JButton("• Liste");
        listBtn.setToolTipText("Aufzählung");

        JButton normalBtn = new JButton("Normal");
        normalBtn.setToolTipText("Normaler Text");

        for (JButton btn : new JButton[]{boldBtn, italicBtn, h1Btn, listBtn, normalBtn}) {
            btn.putClientProperty("JButton.buttonType", "roundRect");
            btn.setFocusable(false);
            toolbar.add(btn);
            toolbar.addSeparator(new Dimension(4, 0));
        }

        // --- Rechtes Panel ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(12, 14, 8, 14));

        rightPanel.add(toolbar, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 4, 0));
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(newButton);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Buttons
        addButton.putClientProperty("JButton.buttonType", "roundRect");
        deleteButton.putClientProperty("JButton.buttonType", "roundRect");
        newButton.putClientProperty("JButton.buttonType", "roundRect");
        addButton.setBackground(new Color(70, 130, 80));
        addButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(150, 50, 50));
        deleteButton.setForeground(Color.WHITE);
        newButton.setBackground(new Color(60, 100, 160));
        newButton.setForeground(Color.WHITE);

        // Datum-Label
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dateLabel.setForeground(Color.GRAY);
        dateLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 4, 0));

        // Titel + Datum + Kategorie
        JLabel titleLabel = new JLabel("Title:");
        JPanel titleInnerPanel = new JPanel(new BorderLayout());
        titleInnerPanel.add(field, BorderLayout.CENTER);
        titleInnerPanel.add(dateLabel, BorderLayout.SOUTH);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(titleInnerPanel, BorderLayout.CENTER);
        titlePanel.add(categoryBox, BorderLayout.SOUTH);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        // Toolbar + titlePanel zusammen oben
        JPanel topRightPanel = new JPanel(new BorderLayout());
        topRightPanel.add(titlePanel, BorderLayout.NORTH);
        topRightPanel.add(toolbar, BorderLayout.SOUTH);
        rightPanel.add(topRightPanel, BorderLayout.NORTH);
        rightPanel.remove(toolbar); // toolbar nur in topRightPanel

        field.setMargin(new Insets(6, 8, 6, 8));

        // --- Linkes Panel ---
        searchField.putClientProperty("JTextField.placeholderText", "Suche...");

        JPanel leftTopPanel = new JPanel(new BorderLayout());
        leftTopPanel.add(filterBox, BorderLayout.NORTH);
        leftTopPanel.add(searchField, BorderLayout.SOUTH);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(leftTopPanel, BorderLayout.NORTH);
        leftPanel.add(list, BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(60, 60, 60)));

        list.setFixedCellHeight(36);
        list.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(rightPanel, BorderLayout.CENTER);
        frame.setVisible(true);

        // --- Daten laden ---
        noteManager.loadNotes();
        for (Note note : noteManager.getNotes()) {
            listModel.addElement(note.getTitle());
        }

        // --- Toolbar Aktionen ---
        boldBtn.addActionListener(e -> {
            HTMLEditorKit kit = (HTMLEditorKit) textPane.getEditorKit();
            kit.getInputAttributes().removeAttribute(HTML.Tag.B);
            new HTMLEditorKit.BoldAction().actionPerformed(e);
            textPane.requestFocus();
        });

        italicBtn.addActionListener(e -> {
            new HTMLEditorKit.ItalicAction().actionPerformed(e);
            textPane.requestFocus();
        });

        h1Btn.addActionListener(e -> {
            wrapSelectionInTag("h1");
            textPane.requestFocus();
        });

        listBtn.addActionListener(e -> {
            wrapSelectionInTag("ul-li");
            textPane.requestFocus();
        });

        normalBtn.addActionListener(e -> {
            // Formatierung zurücksetzen via neuen p-Paragraph
            HTMLDocument doc = (HTMLDocument) textPane.getDocument();
            int start = textPane.getSelectionStart();
            int end = textPane.getSelectionEnd();
            try {
                String selected = textPane.getSelectedText();
                if (selected != null) {
                    doc.remove(start, end - start);
                    doc.insertString(start, selected, null);
                }
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
            textPane.requestFocus();
        });

        // Ctrl+B / Ctrl+I shortcuts
        textPane.getInputMap().put(KeyStroke.getKeyStroke("ctrl B"), "bold");
        textPane.getActionMap().put("bold", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                boldBtn.doClick();
            }
        });
        textPane.getInputMap().put(KeyStroke.getKeyStroke("ctrl I"), "italic");
        textPane.getActionMap().put("italic", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                italicBtn.doClick();
            }
        });

        // --- Listener ---
        addButton.addActionListener(e -> {
            String selectedTitle = (String) list.getSelectedValue();
            if (selectedTitle != null) {
                for (int i = 0; i < noteManager.getNotes().size(); i++) {
                    if (noteManager.getNote(i).getTitle().equals(selectedTitle)) {
                        noteManager.editNote(i, field.getText(), getContent(), (String) categoryBox.getSelectedItem());
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
            textPane.setText("");
            dateLabel.setText("");
            categoryBox.setSelectedIndex(0);
        });

        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedTitle = (String) list.getSelectedValue();
                if (selectedTitle != null) {
                    isLoading = true;
                    for (int i = 0; i < noteManager.getNotes().size(); i++) {
                        if (noteManager.getNote(i).getTitle().equals(selectedTitle)) {
                            Note n = noteManager.getNote(i);
                            field.setText(n.getTitle());
                            textPane.setText(n.getContent());
                            textPane.setCaretPosition(0);
                            categoryBox.setSelectedItem(n.getCategory());
                            dateLabel.setText("Erstellt: " + n.getDate().format(
                                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
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
        textPane.getDocument().addDocumentListener(autoSave);
        field.getDocument().addDocumentListener(autoSave);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                noteManager.saveNotes();
                System.exit(0);
            }
        });
    }

    // HTML-Content aus JTextPane holen
    private String getContent() {
        HTMLDocument doc = (HTMLDocument) textPane.getDocument();
        try {
            java.io.StringWriter sw = new java.io.StringWriter();
            new HTMLEditorKit().write(sw, doc, 0, doc.getLength());
            return sw.toString();
        } catch (Exception e) {
            return textPane.getText();
        }
    }

    // Selektion in HTML-Tag einwickeln
    private void wrapSelectionInTag(String tag) {
        String selected = textPane.getSelectedText();
        if (selected == null || selected.isEmpty()) return;

        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();
        HTMLDocument doc = (HTMLDocument) textPane.getDocument();

        try {
            doc.remove(start, end - start);
            String html;
            if (tag.equals("ul-li")) {
                html = "<ul><li>" + selected + "</li></ul>";
            } else {
                html = "<" + tag + ">" + selected + "</" + tag + ">";
            }
            ((HTMLEditorKit) textPane.getEditorKit()).insertHTML(doc, start, html, 0, 0, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        String content = getContent();
        if (title.isEmpty()) return;

        Note note = new Note(LocalDateTime.now(), title, content, (String) categoryBox.getSelectedItem());
        noteManager.addNote(note);
        listModel.addElement(note.getTitle());
        field.setText("");
        textPane.setText("");
        dateLabel.setText("");
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
                noteManager.editNote(i, field.getText(), getContent(), (String) categoryBox.getSelectedItem());
                listModel.set(list.getSelectedIndex(), field.getText());
                break;
            }
        }
    }
}