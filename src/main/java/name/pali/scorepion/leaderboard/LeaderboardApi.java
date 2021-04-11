package name.pali.scorepion.leaderboard;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:8081")
public class LeaderboardApi {
    private final LeaderboardService leaderboardService;

    public LeaderboardApi(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @PostMapping("/boards")
    public String createBoard(@Valid @RequestBody LeaderboardChangeRequest leaderboardChangeRequest) {
        return leaderboardService.createBoard(leaderboardChangeRequest.getTopN());
    }

    @GetMapping("/boards/{boardKey}")
    public LeaderboardDto getBoard(@PathVariable String boardKey) {
        return leaderboardService.readBoard(boardKey);
    }

    @GetMapping("/boards/{boardKey}/scores")
    public List<Score> getScores(@PathVariable String boardKey) {
        return leaderboardService.getScores(boardKey);
    }

    @PutMapping("/boards/{boardKey}")
    public void updateBoard(@PathVariable String boardKey,
                            @Valid @RequestBody LeaderboardChangeRequest leaderboardChangeRequest) {
        leaderboardService.updateBoard(boardKey, leaderboardChangeRequest);
    }

    @PostMapping("/boards/{boardKey}/scores/clear")
    public void clearScores(@PathVariable String boardKey) {
        leaderboardService.clearScores(boardKey);
    }

    @GetMapping("/boards/{boardKey}/eligible")
    public boolean isEligible(@PathVariable String boardKey, int score) {
        return leaderboardService.scoreEligibleForBoardMembership(boardKey, score);
    }

    @PostMapping("/boards/{boardKey}/scores")
    public void submitScore(@PathVariable String boardKey, @RequestBody Score score) {
        leaderboardService.createScore(boardKey, score);
    }

    @DeleteMapping("/boards/{boardKey}")
    public void deleteBoard(@PathVariable String boardKey) {
        leaderboardService.deleteBoard(boardKey);
    }
}