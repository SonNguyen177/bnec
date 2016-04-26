package ole.com.bnecchampion.modal;

/**
 * Created by nguye on 3/31/2016.
 */
public class WordModel {

    private int id;
    private String word;
    private String pronoun;
    private String description;
    private boolean isCorrect;


    // for gui binding only
    // 0 = normal, 1 = selected, 2 = matched, 3 = not_match
    private int status = 0;
    public static int NORMAL = 0;
    public static int SELECTED = 1;
    public static int MATCHED = 2;
    public static int NOT_MATCH = 3;

    public WordModel() {
    }

    public WordModel(String word, String pronoun) {
        this.word = word;
        this.pronoun = pronoun;
    }

    public WordModel(String word, String pronoun, String description) {
        this.word = word;
        this.pronoun = pronoun;
        this.description = description;
    }

    public WordModel(String word, String pronoun, String description, boolean isCorrect) {
        this.word = word;
        this.pronoun = pronoun;
        this.description = description;
        this.isCorrect = isCorrect;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPronoun() {
        return pronoun;
    }

    public void setPronoun(String pronoun) {
        this.pronoun = pronoun;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        this.isCorrect = correct;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
