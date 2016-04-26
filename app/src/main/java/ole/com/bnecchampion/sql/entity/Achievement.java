package ole.com.bnecchampion.sql.entity;

/**
 * Created by nguye on 4/7/2016.
 */
public class Achievement {
    int id;
    int questionReach;
    int score;
    int failCount;

    public Achievement() {
    }

    public Achievement(int id, int questionReach, int score, int failCount) {
        this.id = id;
        this.questionReach = questionReach;
        this.score = score;
        this.failCount = failCount;
    }

    public Achievement(int questionReach, int score, int failCount) {
        this.questionReach = questionReach;
        this.score = score;
        this.failCount = failCount;
    }

    public int getQuestionReach() {
        return questionReach;
    }

    public void setQuestionReach(int questionReach) {
        this.questionReach = questionReach;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
