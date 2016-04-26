package ole.com.bnecchampion.sql.entity;

/**
 * Created by nguye on 4/7/2016.
 */
public class Question {
    int id;
    String author;
    String hints; // array separate by |
    String explaining;

    public Question(){
    }

    public Question(int id){
        this.id = id;
    }

    public Question(int id, String author, String hints) {
        this.id = id;
        this.author = author;
        this.hints = hints;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHints() {
        return hints;
    }

    public void setHints(String hints) {
        this.hints = hints;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Question(String explaining) {
        this.explaining = explaining;
    }

    public void setExplaining(String explaining) {
        this.explaining = explaining;
    }

    public String getExplaining() {
        return explaining;
    }
}
