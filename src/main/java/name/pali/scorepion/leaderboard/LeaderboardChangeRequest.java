package name.pali.scorepion.leaderboard;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class LeaderboardChangeRequest {

    @Min(3)
    @Max(100)
    private int topN;

    public int getTopN() {
        return topN;
    }

    public void setTopN(int topN) {
        this.topN = topN;
    }
}
