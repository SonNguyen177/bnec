package ole.com.bnecchampion;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ole.com.bnecchampion.file.CSVFile;
import ole.com.bnecchampion.modal.QuestionModel;
import ole.com.bnecchampion.sql.entity.Question;
import ole.com.bnecchampion.sql.entity.Word;
import ole.com.bnecchampion.sql.helper.DatabaseHelper;

/**
 * Created by nguye on 4/13/2016.
 */
public class SpashScreenActivity extends Activity {

    long SPLASH_TIME = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.layout_spash_screen);
        new BackgroundTask().execute();
    }

    private class BackgroundTask extends AsyncTask {
        Intent intent;

        // Database Helper
        DatabaseHelper db;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            intent = new Intent(SpashScreenActivity.this, GamePlayActivity.class);
            db = new DatabaseHelper(getApplicationContext());
        }
        @Override
        protected Object doInBackground(Object[] params) {
            /*  Use this method to load background
            * data that your app needs. */
            try {

                InputStream inputStream = getResources().openRawResource(R.raw.bnec);
                CSVFile csvFile = new CSVFile(inputStream);
                List<String[]> questionList = csvFile.read();

                // delete first
                //db.deleteQuestion(1);
                //db.deleteQuestion(2);

                List<QuestionModel> existQuestions =  db.getAllQuestions();

                for(int i = existQuestions.size(); i< questionList.size(); i++){
                    String[] components = questionList.get(i);

                    QuestionModel q = QuestionModel.parse(components);

                    //  create a question

                    long qId = db.createQuestion(q);
                }

                //
                db.closeDB();

                Thread.sleep(SPLASH_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
                //
                db.closeDB();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
//            Pass your loaded data here using Intent
//            intent.putExtra("data_key", "");
            startActivity(intent);
            finish();
        }
    }
}
