package ole.com.bnecchampion.modal;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nguye on 4/2/2016.
 */
public class QuestionModel {

    private int id;
    private List<WordModel> options;
    private List<String> hints;
    private String author;
    private String explaining;

    public QuestionModel() {
    }

    public static QuestionModel parse(String[] components){
        //"Timestamp",
        // "Option A",
        // "Option B",
        // "Option C",
        // "Option D",
        // "Mark : Select 2/4",
        // "Hint 1",
        // "Hint 2",
        // "Your explaining",
        // "Author"
        //

        QuestionModel retVal = new QuestionModel();

        try {
            retVal.options = new ArrayList<>();
            WordModel word;
            String optionStr;

            // extract optionA
            optionStr = components[1];
            String[] optionElements;
            optionElements = optionStr.split("\\|");

            //
            word = new WordModel(optionElements[0], optionElements[1], optionElements[2]);
            retVal.options.add(word);

            // extract optionB
            optionStr = components[2];
            optionElements = optionStr.split("\\|");

            //
            word = new WordModel(optionElements[0], optionElements[1], optionElements[2]);
            retVal.options.add(word);

            // extract optionC
            optionStr = components[3];
            optionElements = optionStr.split("\\|");

            //
            word = new WordModel(optionElements[0], optionElements[1], optionElements[2]);
            retVal.options.add(word);

            // extract optionD
            optionStr = components[4];
            optionElements = optionStr.split("\\|");

            //
            word = new WordModel(optionElements[0], optionElements[1], optionElements[2]);
            retVal.options.add(word);

            // Mark
            String mark =  components[5];
            if(mark.contains("A")){
                retVal.options.get(0).setCorrect(true);
            }

            if(mark.contains("B")){
                retVal.options.get(1).setCorrect(true);
            }

            if(mark.contains("C")){
                retVal.options.get(2).setCorrect(true);
            }

            if(mark.contains("D")){
                retVal.options.get(3).setCorrect(true);
            }

            // set hints
            List<String> hints = new ArrayList<>();
            hints.add(components[6]);
            hints.add(components[7]);
            retVal.setHints(hints);

            // explaining
            retVal.setExplaining(components[8]);

            retVal.setAuthor(components[9]);

        }catch (Exception ex){
            Log.e("PARSE_CSV", "error when parse a csv row");
        }

        // return
        return retVal;
    }

    public QuestionModel(int id) {
        this.id = id;
    }

    public QuestionModel(int id, List<String> hints, List<WordModel> options) {
        this.id = id;
        this.hints = hints;
        this.options = options;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<WordModel> getOptions() {
        return options;
    }

    public void setOptions(List<WordModel> options) {
        this.options = options;
    }

    public List<String> getHints() {
        return hints;
    }

    public void setHints(List<String> hints) {
        this.hints = hints;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getExplaining() {
        return explaining;
    }

    public void setExplaining(String explaining) {
        this.explaining = explaining;
    }

    private static List<QuestionModel> lst;
    public static List<QuestionModel> generateSampleData(){

        if(lst == null){
            lst = new ArrayList<>();
            QuestionModel question = new QuestionModel(1);
            List<WordModel> words = new ArrayList<>();

            WordModel obj = new WordModel("Base", "/base/", "noun",true);
            words.add(obj);

            obj = new WordModel("Core", "/core/", "noun",true);
            words.add(obj);

            obj = new WordModel("Active", "/active/", "Verb");
            words.add(obj);

            obj = new WordModel("Run", "/run/", "Verb");
            words.add(obj);
            question.setOptions(words);

            // set hint
            List<String> hints = new ArrayList<>();
            hints.add("They have same kind of word.");
            hints.add("The meaning is same root or platform.");
            question.setHints(hints);

            // add to list
            lst.add(question);

        }
        return lst;
    }
}
