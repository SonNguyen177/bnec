package ole.com.bnecchampion;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.design.widget.CoordinatorLayout;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ole.com.bnecchampion.adapter.WordRecylerAdapter;
import ole.com.bnecchampion.modal.QuestionModel;
import ole.com.bnecchampion.modal.WordModel;
import ole.com.bnecchampion.sql.entity.Question;
import ole.com.bnecchampion.sql.entity.Word;
import ole.com.bnecchampion.sql.helper.DatabaseHelper;

public class GamePlayActivity extends AppCompatActivity {

    @Bind(R.id.rv)
    RecyclerView rv;

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @Bind(R.id.tvQuestion) TextView tvQuestion;
    @Bind(R.id.tvCountdown) TextView tvCountdown;
    @Bind(R.id.tvScore) TextView tvScore;
    @Bind(R.id.tvHint) TextView tvHint;
    @Bind(R.id.panelTips)
    LinearLayout panelTips;

    @Bind(R.id.imageIdea)
    ImageView imageIcon;

    List<QuestionModel> questionSet;
    QuestionModel questionData;

    List optionSelectedIdx;

    View.OnClickListener mOnClickListener;
    WordRecylerAdapter adapter;

    CountDownTimer countDownTimer;
    boolean isFinish;

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
                if(optionSelectedIdx.size() ==2){
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
                            finishQuestion(true);
                        } else {
                            currentChoice.setStatus(WordModel.NOT_MATCH);
                            firstChoice.setStatus(WordModel.NOT_MATCH);

                            //
                            finishQuestion(false);
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
        countDownTimer =  new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                long secondLeft = millisUntilFinished / 1000;

                if(secondLeft<10) {
                    tvCountdown.setText("0" + Long.toString((secondLeft)));
                  //  tvCountdown.setTextColor(getResources().getColor(R.color.bnecFourth));
                }else{
                    tvCountdown.setText(Long.toString((secondLeft)));
                }

                if(secondLeft == 20){
                    showHint(0);
                }
                else if(secondLeft == 10){
                    showHint(1);
                }
            }

            public void onFinish() {
                tvCountdown.setText("00");
               // tvCountdown.setTextColor(getResources().getColor(R.color.failColor));

                // stop question with failed
                finishQuestion(false);
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

    private void showHint(int idx){
        panelTips.setVisibility(View.VISIBLE);
        Animation animation  =  AnimationUtils.loadAnimation(this, R.anim.hint_animation);
        imageIcon.startAnimation(animation);
        String hint = questionData.getHints().get(idx);
        if(idx == 0){
            tvHint.setText( ++idx +". " + hint);
        }else{
            tvHint.setText(tvHint.getText() + "\n" + ++idx +". " + hint);
        }

//        Snackbar snackbar = Snackbar
//                .make(coordinatorLayout, hint, Snackbar.LENGTH_INDEFINITE);
//        snackbar.setDuration(9000); // 9 seconds
//                //.setAction("Undo", mOnClickListener);
//        snackbar.setActionTextColor(getResources().getColor(R.color.bnecFirst));
//        View snackbarView = snackbar.getView();
//        snackbarView.setBackgroundColor(getResources().getColor(R.color.bnecFourth));
//        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
//        textView.setTextColor(Color.WHITE);
//        snackbar.show();
    }

    private void showQuestion(){

        isFinish = false;
        optionSelectedIdx.clear();
        tvCountdown.setTextColor(Color.WHITE);
      //  tvCountdown.setTextColor(getResources().getColor(R.color.bnecThird));
        Animation animation  =  AnimationUtils.loadAnimation(this, R.anim.anim_countdown);
        tvCountdown.startAnimation(animation);
        // refresh
        adapter.setData(questionData.getOptions());
        adapter.notifyDataSetChanged();
        countDownTimer.start();

        panelTips.setVisibility(View.GONE);

    }

    private void finishQuestion(boolean isRight){

        isFinish = true;
        countDownTimer.cancel();
        tvCountdown.clearAnimation();
        imageIcon.clearAnimation();
        if (isRight){
            //
        }else{

        }
    }

    private void getCurrentQuestion(){
        if(db == null) {
            db = new DatabaseHelper(getApplicationContext());
        }
        questionSet = db.getAllQuestions();

        questionData =  questionSet.get(0);// test with first

        //  db.closeDB();

        // show question
        showQuestion();
    }

}
