package name.pali.scorepion.leaderboard;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("leaderboard")
public class Leaderboard {
    @Id
    private String id;
    private int topN;

    public int getTopN() {
        return topN;
    }

    public String getId() {
        return id;
    }

    public void setTopN(int topN) {
        this.topN = topN;
    }

    public Leaderboard(int topN) {
        this.topN = topN;
    }
}
