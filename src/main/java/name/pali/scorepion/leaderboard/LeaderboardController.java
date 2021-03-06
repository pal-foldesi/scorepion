package name.pali.scorepion.leaderboard;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import name.pali.scorepion.config.CorsAllowedOrigins;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Leaderboard")
@RestController
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @CrossOrigin(origins = CorsAllowedOrigins.LOCAL, methods = RequestMethod.POST)
    @PostMapping("/boards")
    public String createBoard(@Valid @RequestBody LeaderboardChangeRequest leaderboardChangeRequest) {
        return leaderboardService.createBoard(leaderboardChangeRequest.getTopN());
    }

    @CrossOrigin(origins = {CorsAllowedOrigins.LOCAL, CorsAllowedOrigins.REMOTE}, methods = RequestMethod.GET)
    @GetMapping("/boards/{boardKey}")
    public LeaderboardDto getBoard(@PathVariable String boardKey) {
        return leaderboardService.readBoard(boardKey);
    }

    @CrossOrigin(origins = {CorsAllowedOrigins.LOCAL, CorsAllowedOrigins.REMOTE}, methods = RequestMethod.GET)
    @GetMapping("/boards/{boardKey}/scores")
    public List<Score> getScores(@PathVariable String boardKey) {
        return leaderboardService.getScores(boardKey);
    }

    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @CrossOrigin(origins = CorsAllowedOrigins.LOCAL, methods = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/boards/{boardKey}")
    public void updateBoard(@PathVariable String boardKey,
                            @Valid @RequestBody LeaderboardChangeRequest leaderboardChangeRequest) {
        leaderboardService.updateBoard(boardKey, leaderboardChangeRequest);
    }

    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @CrossOrigin(origins = CorsAllowedOrigins.LOCAL, methods = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/boards/{boardKey}/scores/clear")
    public void clearScores(@PathVariable String boardKey) {
        leaderboardService.clearScores(boardKey);
    }

    @CrossOrigin(origins = {CorsAllowedOrigins.LOCAL, CorsAllowedOrigins.REMOTE}, methods = RequestMethod.GET)
    @GetMapping("/boards/{boardKey}/eligibility")
    public boolean checkEligibility(@PathVariable String boardKey, @RequestParam int score) {
        return leaderboardService.scoreEligibleForBoardMembership(boardKey, score);
    }

    @CrossOrigin(origins = {CorsAllowedOrigins.LOCAL, CorsAllowedOrigins.REMOTE}, methods = RequestMethod.POST)
    @PostMapping("/boards/{boardKey}/scores")
    public void submitScore(@PathVariable String boardKey, @RequestBody Score score) {
        leaderboardService.createScore(boardKey, score);
    }

    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @CrossOrigin(origins = CorsAllowedOrigins.LOCAL, methods = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/boards/{boardKey}")
    public void deleteBoard(@PathVariable String boardKey) {
        leaderboardService.deleteBoard(boardKey);
    }
}