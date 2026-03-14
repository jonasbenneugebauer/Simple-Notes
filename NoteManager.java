import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class NoteManager {

    private ArrayList<Note> notes = new ArrayList<>();
    private static final String FILE_PATH = "notes.txt";

    public void addNote(Note note) {
        notes.add(note);
    }

    public void deleteNote(int index) {
        notes.remove(index);
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public void editNote(int index, String newTitle, String newContent){
        Note note = notes.get(index);
        note.setTitle(newTitle);
        note.setContent(newContent);
    }

    public Note getNote(int index) {
        return notes.get(index);
    }

    public void saveNotes() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))){
            for(Note note : notes){
                writer.write("---");
                writer.newLine();
                writer.write("Title: " + note.getTitle());  
                writer.newLine();
                writer.write("Date: " + note.getDate().toString()); 
                writer.newLine();
                writer.write("Content: " + note.getContent());  
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving notes: " + e.getMessage());
        }
    }

   public void loadNotes() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return; // Beim ersten Start noch keine Datei

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            String title = null, content = null;
            LocalDateTime dateTime = null;

            while ((line = reader.readLine()) != null) {
                if (line.equals("---")) {
                    // Vorherige Note speichern falls vorhanden
                    if (title != null) {
                        notes.add(new Note(dateTime, title, content));
                    }
                    title = content = null;
                    dateTime = null;
                } else if (line.startsWith("Titel: ")) {
                    title = line.substring(7);
                } else if (line.startsWith("Datum: ")) {
                    dateTime = LocalDateTime.parse(line.substring(7));
                } else if (line.startsWith("Inhalt: ")) {
                    content = line.substring(8);
                }
            }
            // Letzte Note nicht vergessen
            if (title != null) {
                notes.add(new Note(dateTime, title, content));
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Laden: " + e.getMessage());
        }
    }
}
