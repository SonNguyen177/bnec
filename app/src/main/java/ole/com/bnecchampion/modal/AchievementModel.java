package ole.com.bnecchampion.modal;

/**
 * Created by nguye on 4/28/2016.
 */
public class AchievementModel {

    public static String QUESTION_PASSED;
    public static String TOTAL_SCORE;
    public static String TOTAL_FAILED;

    private int questionPass;
    private int totalScore;
    private int totalFailed;

    public AchievementModel(){

    }

    public AchievementModel(int questionPass, int totalScore, int totalFailed) {
        this.questionPass = questionPass;
        this.totalScore = totalScore;
        this.totalFailed = totalFailed;
    }

    public int getQuestionPass() {
        return questionPass;
    }

    public void setQuestionPass(int questionPass) {
        this.questionPass = questionPass;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getTotalFailed() {
        return totalFailed;
    }

    public void setTotalFailed(int totalFailed) {
        this.totalFailed = totalFailed;
    }
}
