package name.pali.scorepion.leaderboard;

import java.util.List;

public class LeaderboardDto {
    private final int topN;
    private final List<Score> scores;

    public List<Score> getScores() {
        return scores;
    }

    public int getTopN() {
        return topN;
    }

    public LeaderboardDto(int topN, List<Score> scores) {
        this.topN = topN;
        this.scores = scores;
    }
}
