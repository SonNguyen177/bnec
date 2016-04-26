package ole.com.bnecchampion.sql.entity;

/**
 * Created by nguye on 4/7/2016.
 */
public class Word {
    int id;
    private int questionId;
    String word;
    String pronoun;
    String description;
    boolean isCorrect;

    public Word() {
    }

    public Word(int id) {
        this.id = id;
    }

    public Word(String word, String pronoun, String description) {
        this.word = word;
        this.pronoun = pronoun;
        this.description = description;
    }

    public Word(String word, String pronoun, String description, boolean isCorrect) {
        this.word = word;
        this.pronoun = pronoun;
        this.description = description;
        this.isCorrect = isCorrect;
    }

    public Word(int id, int questionId) {
        this.id = id;
        this.questionId = questionId;
    }

    public Word(int id, int questionId, String word, String pronoun, String description, boolean isCorrect) {
        this.id = id;
        this.questionId = questionId;
        this.word = word;
        this.pronoun = pronoun;
        this.description = description;
        this.isCorrect = isCorrect;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
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

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}
