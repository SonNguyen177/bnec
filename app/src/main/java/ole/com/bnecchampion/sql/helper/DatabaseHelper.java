package ole.com.bnecchampion.sql.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ole.com.bnecchampion.modal.QuestionModel;
import ole.com.bnecchampion.modal.WordModel;
import ole.com.bnecchampion.sql.entity.Achievement;
import ole.com.bnecchampion.sql.entity.Question;
import ole.com.bnecchampion.sql.entity.Word;

/**
 * Created by nguye on 4/7/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 11;

    // Database Name
    private static final String DATABASE_NAME = "bnec";

    // Table Names
    private static final String TABLE_QUESTION = "questions";
    private static final String TABLE_WORD = "words";
    private static final String TABLE_ACHIEVEMENT = "achievement";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_QUESTION_ID = "question_id";

    // QUESTION Table - column nmaes
    private static final String KEY_HINTS = "hints";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_EXPLAINING = "explaining";

    // WORD Table - column names
    private static final String KEY_WORD = "word";
    private static final String KEY_PRONOUN = "pronoun";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IS_CORRECT = "is_correct";

    // ACHIEVEMENT Table - column names
    private static final String KEY_QUESTION_REACH = "question_reach";
    private static final String KEY_SCORE = "score";
    private static final String KEY_FAIL_COUNT = "fail_count";

    // Table Create Statements
    private static final String CREATE_TABLE_QUESTION = "CREATE TABLE "
            + TABLE_QUESTION + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_HINTS
            + " TEXT," + KEY_AUTHOR + " TEXT," + KEY_EXPLAINING + " TEXT"  + ")";

    //  table create statement
    private static final String CREATE_TABLE_WORD = "CREATE TABLE " + TABLE_WORD
            + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_WORD + " TEXT,"
            + KEY_PRONOUN + " TEXT,"
            + KEY_DESCRIPTION + " TEXT,"
            + KEY_IS_CORRECT + " INTEGER,"
            + KEY_QUESTION_ID + " INTEGER"
            + ")";

    // todo_tag table create statement
    private static final String CREATE_TABLE_ACHIEVEMENT = "CREATE TABLE "
            + TABLE_ACHIEVEMENT + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_QUESTION_REACH + " INTEGER,"
            + KEY_SCORE + " INTEGER,"
            + KEY_FAIL_COUNT + " INTEGER"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(CREATE_TABLE_QUESTION);
        db.execSQL(CREATE_TABLE_WORD);
        db.execSQL(CREATE_TABLE_ACHIEVEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACHIEVEMENT);

        // create new tables
        onCreate(db);
    }


    //region Question
    public long createQuestion(QuestionModel question){

        SQLiteDatabase db = this.getWritableDatabase();

        Question q = new Question();
        StringBuilder sb = new StringBuilder();

       for(int i=0; i< question.getHints().size(); i++){
           sb.append(question.getHints().get(i));
           if(i != question.getHints().size() - 1){
               sb.append("|");
           }
       }
        q.setHints(sb.toString());
        q.setAuthor(question.getAuthor());
        q.setExplaining(question.getExplaining());


        List<Word> words = new ArrayList<>();

        for (WordModel w : question.getOptions()) {
            Word obj = new Word(w.getWord(), w.getPronoun(), w.getDescription(),w.isCorrect());
            words.add(obj);
        }

        ContentValues values = new ContentValues();
        values.put(KEY_AUTHOR, q.getAuthor());
        values.put(KEY_HINTS, q.getHints());
        values.put(KEY_EXPLAINING, q.getExplaining());

        // insert row
        long questionId = db.insert(TABLE_QUESTION, null, values);

        // assigning words for question
        for (Word w : words) {
            createWord(questionId,w);
        }

        return questionId;
    }

    /*
 * Updating a Question
 */
    public int updateQuestion(Question q) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_AUTHOR, q.getAuthor());
        values.put(KEY_HINTS, q.getHints());
        values.put(KEY_EXPLAINING, q.getExplaining());

        // updating row
        return db.update(TABLE_QUESTION, values, KEY_ID + " = ?",
                new String[] { String.valueOf(q.getId()) });
    }

    /*
 * Deleting a Question
 */
    public void deleteQuestion(long questionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_QUESTION, KEY_ID + " = ?",
                new String[] { String.valueOf(questionId) });

        // delete words
       List<WordModel> words = getListWordsOfQuestion(questionId);
        for(WordModel w : words){
            db.delete(TABLE_WORD, KEY_ID + " = ?",
                    new String[] { String.valueOf(w.getId()) });
        }
    }

    /*
 * get single question
 */
    public Question getQuestion(long questionId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_QUESTION + " WHERE "
                + KEY_ID + " = " + questionId;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Question q = new Question();
        q.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        q.setAuthor((c.getString(c.getColumnIndex(KEY_AUTHOR))));
        q.setHints(c.getString(c.getColumnIndex(KEY_HINTS)));
        q.setExplaining(c.getString(c.getColumnIndex(KEY_EXPLAINING)));

        return q;
    }

    /*
 * getting all Questions
 * */
    public List<QuestionModel> getAllQuestions() {
        List<QuestionModel> retVals = new ArrayList<QuestionModel>();
        String selectQuery = "SELECT  * FROM " + TABLE_QUESTION;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                QuestionModel q = new QuestionModel();
                q.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                q.setAuthor((c.getString(c.getColumnIndex(KEY_AUTHOR))));
                //
                q.setExplaining((c.getString(c.getColumnIndex(KEY_EXPLAINING))));

                // parse by |
                String[] hints =  c.getString(c.getColumnIndex(KEY_HINTS)).split("\\|");
                List<String> hintList = new ArrayList<>();
                for(String s : hints){
                    hintList.add(s);
                }
                q.setHints(hintList);

                // set words
                q.setOptions(getListWordsOfQuestion(q.getId()));

                // adding
                retVals.add(q);
            } while (c.moveToNext());
        }

        Log.e(LOG, "getAllQuestions count = " + retVals.size());
        return retVals;
    }

    //endregion

    //region Word
    public long createWord(long questionID, Word word) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_WORD, word.getWord());
        values.put(KEY_PRONOUN, word.getPronoun());
        values.put(KEY_DESCRIPTION, word.getDescription());
        values.put(KEY_IS_CORRECT, word.getWord());
        values.put(KEY_QUESTION_ID, questionID); // FK

        // insert row
        long wordId = db.insert(TABLE_WORD, null, values);

        return wordId;
    }

    public List<WordModel> getListWordsOfQuestion(long questionId){
        List<WordModel> retVals = new ArrayList<WordModel>();
        String selectQuery = "SELECT  * FROM " + TABLE_WORD + "  WHERE " + KEY_QUESTION_ID +"=" + questionId;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                WordModel obj = new WordModel();
              //  obj.setQuestionId((int)questionId);
                obj.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                obj.setWord((c.getString(c.getColumnIndex(KEY_WORD))));
                obj.setPronoun(c.getString(c.getColumnIndex(KEY_PRONOUN)));
                obj.setDescription(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
                boolean isCorrect = false;
                isCorrect = c.getInt(c.getColumnIndex(KEY_IS_CORRECT)) == 1 ? true : false;
                obj.setCorrect(isCorrect);

                // adding
                retVals.add(obj);
            } while (c.moveToNext());
        }

        return retVals;
    }
//endregion

    //region Achievement
    public long createAchievement(Achievement ach) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_QUESTION_REACH, ach.getQuestionReach());
        values.put(KEY_SCORE, ach.getScore());
        values.put(KEY_FAIL_COUNT, ach.getFailCount());

        // insert row
        long wordId = db.insert(TABLE_ACHIEVEMENT, null, values);

        return wordId;
    }

    //region Achievement
    public int updateAchievement(Achievement ach) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_QUESTION_REACH, ach.getQuestionReach());
        values.put(KEY_SCORE, ach.getScore());
        values.put(KEY_FAIL_COUNT, ach.getFailCount());

        // insert row
        return db.update(TABLE_ACHIEVEMENT, null, KEY_ID + " = ?",
                new String[] { String.valueOf(ach.getId()) });
    }

    public Achievement getAchievement(long achievementId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_ACHIEVEMENT + " WHERE "
                + KEY_ID + " = " + achievementId;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Achievement obj = new Achievement();
        obj.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        obj.setFailCount((c.getInt(c.getColumnIndex(KEY_FAIL_COUNT))));
        obj.setQuestionReach(c.getInt(c.getColumnIndex(KEY_QUESTION_REACH)));
        obj.setScore(c.getInt(c.getColumnIndex(KEY_SCORE)));

        return obj;
    }

    //endregion
    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }


}
