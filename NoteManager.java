import java.util.ArrayList;

public class NoteManager {

    private ArrayList<Note> notes = new ArrayList<>();

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

}
