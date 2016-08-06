package ole.com.bnecchampion;

import android.animation.Animator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewAnimationUtils;
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
import me.drakeet.materialdialog.MaterialDialog;
import ole.com.bnecchampion.adapter.WordRecylerAdapter;
import ole.com.bnecchampion.modal.AchievementModel;
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

    @Bind(R.id.fabPlay)
    FloatingActionButton fabPlay;

    Snackbar snackbar;
    MaterialDialog mMaterialDialog;

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

    AchievementModel achievementModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        // bind
        ButterKnife.bind(this);

//        MaterialRippleLayout.on(getcon)
//                .rippleColor(Color.BLACK)
//                .create();

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

//                // itemView.setElevation(20.0f);
//
//                // get the center for the clipping circle
//                int cx = itemView.getWidth() / 2;
//                int cy = itemView.getHeight() / 2;
//
//// get the final radius for the clipping circle
//                float finalRadius = (float) Math.hypot(cx, cy);
//
//// create the animator for this view (the start radius is zero)
//                Animator anim = ViewAnimationUtils.createCircularReveal(itemView, cx, cy, 0, finalRadius);
//
//// make the view visible and start the animation
//               // itemView.setVisibility(View.VISIBLE);
//                anim.start();

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
                fabPlay.setEnabled(false);

                if (secondLeft < 10) {
                    tvCountdown.setText("0" + Long.toString((secondLeft)));
                    tvCountdown.setTextColor(getResources().getColor(R.color.failColor));
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

                if (isRight) {
                    Toast.makeText(GamePlayActivity.this, "Share your score!", Toast.LENGTH_SHORT).show();
                    mMaterialDialog.dismiss();
                } else {

                    // show right answer
                    // Toast.makeText(GamePlayActivity.this, "Share your score!", Toast.LENGTH_SHORT).show();
                    for (WordModel w : questionData.getOptions()) {
                        if (w.isCorrect()) {
                            w.setStatus(WordModel.MATCHED);
                        } else {
                            w.setStatus(WordModel.NORMAL);
                        }
                    }
                    // refresh
                    adapter.notifyDataSetChanged();
                    mMaterialDialog.dismiss();
                }
            }
        };

        //
        panelTips.setAlpha(0);

        // show last result
        // test only
        achievementModel = new AchievementModel();


        //achievementModel =  getAchievement();

        showAchievement();

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
                sb.append("\n" + getResources().getString(R.string.explain) + "\n" + questionData.getExplaining());
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
        tvCountdown.setTextColor(getResources().getColor(R.color.warningColor));
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
        // 3. Show result & Play float button bar
        // 4. Record score, save data

        countDownTimer.cancel();
        tvCountdown.clearAnimation();
        imageIcon.clearAnimation();
        fabPlay.setEnabled(true);

        // 1.
        // disable select :
        optionSelectedIdx.clear();
        optionSelectedIdx.add("0");
        optionSelectedIdx.add("0");
//        for (WordModel w : questionData.getOptions()) {
//            if (w.isCorrect()) {
//                w.setStatus(WordModel.MATCHED);
//            } else {
//                w.setStatus(WordModel.NORMAL);
//            }
//        }
//        // refresh
//        adapter.notifyDataSetChanged();

        // 2.
        showHint(SHOW_ALL_HINT);

        // 3.
        panelFloatButton.setAlpha(0.7f);
        if (isRight) {
            //
            String strFormat;
            if (secondLeft >= 20) {
                strFormat = getResources().getString(R.string.great_alert);
            } else if (secondLeft >= 10) {
                strFormat = getResources().getString(R.string.cool_alert);
            } else {
                strFormat = getResources().getString(R.string.good_alert);
            }

            String strMsg = String.format(strFormat, secondLeft);

//            snackbar = Snackbar.make(coordinatorLayout, strMsg, Snackbar.LENGTH_LONG);
//            // snackbar.setDuration(5000); // 9 seconds
//            snackbar.setAction(getResources().getString(R.string.share), mOnClickListener);
//            snackbar.setActionTextColor(getResources().getColor(R.color.bnecFourth));
//            View snackbarView = snackbar.getView();
//            snackbarView.setBackgroundColor(getResources().getColor(R.color.bnecFirst));
//            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
//            textView.setTextColor(Color.WHITE);
//            snackbar.show();

             mMaterialDialog = new MaterialDialog(this)
                    .setTitle(getResources().getString(R.string.congratulation))
                    .setMessage(strMsg)
                    .setPositiveButton(getResources().getString(R.string.share), mOnClickListener)
                    .setNegativeButton(getResources().getString(R.string.cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();
                        }
                    });

            mMaterialDialog.show();
        } else {
//            snackbar = Snackbar.make(coordinatorLayout, getResources().getString(R.string.sorry_alert), Snackbar.LENGTH_LONG);
//            snackbar.setAction(getResources().getString(R.string.result), mOnClickListener);
//            snackbar.show();

            mMaterialDialog = new MaterialDialog(this)
                    .setTitle(getResources().getString(R.string.let_try))
                    .setMessage(getResources().getString(R.string.sorry_alert))
                    .setPositiveButton(getResources().getString(R.string.result), mOnClickListener)
                    .setNegativeButton(getResources().getString(R.string.cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();
                        }
                    });

            mMaterialDialog.show();
        }

        // 4.

        achievementModel.setQuestionPass(achievementModel.getQuestionPass() + 1);

        if (isRight) {
            achievementModel.setTotalScore(achievementModel.getTotalScore() + (int) secondLeft);
        } else {
            achievementModel.setTotalFailed(achievementModel.getTotalFailed() + 1);
        }
        // save
        saveAchievement(achievementModel);
        showAchievement();
    }

    @OnClick(R.id.fabPlay)
    public void playAction() {
        //Toast.makeText(this, "Play", Toast.LENGTH_SHORT).show();

//        if (snackbar != null) {
//            snackbar.dismiss();
//        }
        getCurrentQuestion();
    }


    private void showAchievement() {
        String questionStr;
        if (achievementModel.getQuestionPass() < 10) {
            questionStr = "0" + achievementModel.getQuestionPass();
        } else {
            questionStr = "" + achievementModel.getQuestionPass();
        }
        tvQuestion.setText(questionStr);

        //
        String scoreStr;
        if (achievementModel.getTotalScore() < 100) {
            scoreStr = "0" + achievementModel.getTotalScore();
        } else {
            scoreStr = "" + achievementModel.getTotalScore();
        }
        tvScore.setText(scoreStr);
    }

    private void getCurrentQuestion() {
        if (db == null) {
            db = new DatabaseHelper(getApplicationContext());
        }
        if(questionSet == null) {
            questionSet = db.getAllQuestions();
        }
        int nextQuestion = achievementModel.getQuestionPass();

        if (nextQuestion >= questionSet.size()) {
            Toast.makeText(this, getResources().getString(R.string.end_question_set), Toast.LENGTH_SHORT).show();
            return;
        }

        questionData = questionSet.get(nextQuestion);

        db.closeDB();

        // show question
        showQuestion();
    }

    //region Achievement

    public AchievementModel getAchievement() {
        AchievementModel retVal;
        SharedPreferences sharedPref = GamePlayActivity.this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        int defaultValue = 0;

        int questionPassed = sharedPref.getInt(AchievementModel.QUESTION_PASSED, defaultValue);
        int totalScore = sharedPref.getInt(AchievementModel.TOTAL_SCORE, defaultValue);
        int totalFailed = sharedPref.getInt(AchievementModel.TOTAL_FAILED, defaultValue);

        retVal = new AchievementModel(questionPassed, totalScore, totalFailed);

        return retVal;
    }

    public void saveAchievement(AchievementModel info) {
        SharedPreferences sharedPref = GamePlayActivity.this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(AchievementModel.QUESTION_PASSED, info.getQuestionPass());
        editor.putInt(AchievementModel.TOTAL_SCORE, info.getTotalScore());
        editor.putInt(AchievementModel.TOTAL_FAILED, info.getTotalFailed());
        editor.commit();

    }
    //endregion

}
