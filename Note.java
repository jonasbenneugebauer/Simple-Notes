import java.time.LocalDateTime;

public class Note {
    private LocalDateTime date;
    private String title;
    private String content;
    private String category;

    public Note(LocalDateTime date, String title, String content, String category) {
        this.date = date;
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
