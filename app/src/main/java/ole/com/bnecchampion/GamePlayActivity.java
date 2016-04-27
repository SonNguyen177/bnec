package ole.com.bnecchampion;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ole.com.bnecchampion.adapter.WordRecylerAdapter;
import ole.com.bnecchampion.modal.QuestionModel;
import ole.com.bnecchampion.modal.WordModel;
import ole.com.bnecchampion.sql.entity.Question;
import ole.com.bnecchampion.sql.entity.Word;
import ole.com.bnecchampion.sql.helper.DatabaseHelper;

public class GamePlayActivity extends AppCompatActivity {

    final int SHOW_ALL_HINT = -1; // show all hints + explain

    @Bind(R.id.rv)
    RecyclerView rv;

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @Bind(R.id.tvQuestion)
    TextView tvQuestion;
    @Bind(R.id.tvCountdown)
    TextView tvCountdown;
    @Bind(R.id.tvScore)
    TextView tvScore;
    @Bind(R.id.tvHint)
    TextView tvHint;

    @Bind(R.id.panelTips)
    LinearLayout panelTips;

    @Bind(R.id.panelFloatButton)
    LinearLayout panelFloatButton;


    @Bind(R.id.imageIdea)
    ImageView imageIcon;

    List<QuestionModel> questionSet;
    QuestionModel questionData;

    List optionSelectedIdx;

    View.OnClickListener mOnClickListener;
    WordRecylerAdapter adapter;

    CountDownTimer countDownTimer;
    long secondLeft;
    boolean isRight;

    // Database Helper
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        // bind
        ButterKnife.bind(this);

        optionSelectedIdx = new ArrayList();

        //

        // custom animation
        //  rvChapter.setItemAnimator(new SlideInUpAnimator());

        // optimized
        rv.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rv.setLayoutManager(gridLayoutManager);


        // set adapter
        adapter = new WordRecylerAdapter(this, new ArrayList<WordModel>() {
        });

        // process choice
        adapter.setOnItemClickListener(new WordRecylerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {

                // test show hint
                if (optionSelectedIdx.size() == 2) {
                    return;
                }

                WordModel currentChoice = questionData.getOptions().get(position);

                if (optionSelectedIdx.size() > 0) {

                    int firstIdx = Integer.parseInt(optionSelectedIdx.get(0).toString());

                    WordModel firstChoice = questionData.getOptions().get(firstIdx);

                    if (firstIdx == position) {
                        optionSelectedIdx.clear();
                        currentChoice.setStatus(WordModel.NORMAL); // not select
                    } else {

                        // show result
                        optionSelectedIdx.add(String.valueOf(position));

                        if (firstChoice.isCorrect() && currentChoice.isCorrect()) {
                            currentChoice.setStatus(WordModel.MATCHED);
                            firstChoice.setStatus(WordModel.MATCHED);

                            //
                            isRight = true;
                            finishQuestion();
                        } else {
                            currentChoice.setStatus(WordModel.NOT_MATCH);
                            firstChoice.setStatus(WordModel.NOT_MATCH);

                            //
                            isRight = false;
                            finishQuestion();
                        }
                        // Toast.makeText(GamePlayActivity.this, "You choosen [" + firstChoice.getWord() + "] & " + "[" + currentChoice.getWord() + "]", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    optionSelectedIdx.add(String.valueOf(position));
                    currentChoice.setStatus(WordModel.SELECTED);
                }

                // refresh
                adapter.notifyDataSetChanged();
            }
        });

        // set adapter
        rv.setAdapter(adapter);
        //

        // create countdown timer
        countDownTimer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                secondLeft = millisUntilFinished / 1000;

                if (secondLeft < 10) {
                    tvCountdown.setText("0" + Long.toString((secondLeft)));
                    //  tvCountdown.setTextColor(getResources().getColor(R.color.bnecFourth));
                } else {
                    tvCountdown.setText(Long.toString((secondLeft)));
                }

                if (secondLeft == 20) {
                    showHint(0);
                } else if (secondLeft == 10) {
                    showHint(1);
                }
            }

            public void onFinish() {
                tvCountdown.setText("00");
                // tvCountdown.setTextColor(getResources().getColor(R.color.failColor));

                // stop question with failed
                isRight = false;
                finishQuestion();
            }
        };

        // config snackbar
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

        //
        getCurrentQuestion();
    }

    private void showHint(int idx) {
        // panelTips.setVisibility(View.VISIBLE);
        panelTips.setAlpha(1);

        if (imageIcon.getAnimation() == null) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.hint_animation);
            imageIcon.startAnimation(animation);
        }

        String hint;
        if (idx == SHOW_ALL_HINT) {
            tvHint.setText("");
            StringBuilder sb = new StringBuilder();
            hint = questionData.getHints().get(++idx);

            sb.append(++idx + ". " + hint);
            hint = questionData.getHints().get(idx);
            sb.append("\n" + ++idx + ". " + hint);

            // explain
            if (questionData.getExplaining().length() > 0) {
                sb.append("\n" + questionData.getAuthor() + R.string.explain + "\n" + questionData.getExplaining());
            }
            sb.append("\n" + "-- Thank you, " + questionData.getAuthor() + " --");
            tvHint.setText(sb.toString());

        } else if (idx == 0) {
            hint = questionData.getHints().get(idx);
            tvHint.setText(++idx + ". " + hint);
        } else if (idx == 1) {
            hint = questionData.getHints().get(idx);
            tvHint.setText(tvHint.getText() + "\n" + ++idx + ". " + hint);
        }
    }

    private void showQuestion() {

        // Show new question = next action
        // 1. Reset variable, gui, timer, hide hints & float button for new question
        //

        secondLeft = 30;
        isRight = false;
        optionSelectedIdx.clear();
        tvCountdown.setTextColor(Color.WHITE);
        //  tvCountdown.setTextColor(getResources().getColor(R.color.bnecThird));
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_countdown);
        tvCountdown.startAnimation(animation);
        // refresh
        adapter.setData(questionData.getOptions());
        adapter.notifyDataSetChanged();
        countDownTimer.start();

        // panelTips.setVisibility(View.GONE);
        panelTips.setAlpha(0);
        panelFloatButton.setAlpha(1);
        tvHint.setText("");

    }

    private void finishQuestion() {

        // Finish question
        // 1. Show result match word
        // 2. Show all hint, author explain
        // 3. Show dialog result & Next / Stop float button bar
        // 4. Record score, save data

        countDownTimer.cancel();
        tvCountdown.clearAnimation();
        imageIcon.clearAnimation();

        // 1.
        for (WordModel w : questionData.getOptions()) {
            if (w.isCorrect()) {
                w.setStatus(WordModel.MATCHED);
            } else {
                w.setStatus(WordModel.NORMAL);
            }
        }
        // refresh
        adapter.notifyDataSetChanged();

        // 2.
        showHint(SHOW_ALL_HINT);

        // 3.
        panelFloatButton.setAlpha(0.7f);
        if (isRight) {
            //

            String strFormat = getResources().getString(R.string.great_alert);
            String strMsg = String.format(strFormat, secondLeft);

            Snackbar snackbar = Snackbar.make(coordinatorLayout,strMsg , Snackbar.LENGTH_LONG);
            // snackbar.setDuration(5000); // 9 seconds
            //.setAction("Undo", mOnClickListener);
//            snackbar.setActionTextColor(getResources().getColor(R.color.bnecFirst));
//            View snackbarView = snackbar.getView();
//            snackbarView.setBackgroundColor(getResources().getColor(R.color.bnecFourth));
//            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
//            textView.setTextColor(Color.WHITE);
            snackbar.show();
        } else {

        }

        // 4.
    }

    private void showFloatButton() {

    }

    @OnClick(R.id.fabPlay)
    public void playAction() {
        Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show();
    }

//    @OnClick(R.id.fabStop)
//    public void stopAction(){
//        Toast.makeText(this,"Stop",Toast.LENGTH_SHORT).show();
//    }

    private void showResultDialog() {

    }

    private void showAchievement() {

    }

    private void saveAchievement() {

    }

    private void getCurrentQuestion() {
        if (db == null) {
            db = new DatabaseHelper(getApplicationContext());
        }
        questionSet = db.getAllQuestions();

        questionData = questionSet.get(0);// test with first

        db.closeDB();

        // show question
        showQuestion();
    }

}
