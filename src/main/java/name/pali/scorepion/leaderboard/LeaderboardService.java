package name.pali.scorepion.leaderboard;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {
    private final LeaderboardRepository leaderboardRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisScript<Void> script;
    private static final String SCORES_SET_PREFIX = "scores:";
    private static final String BOARD_NOT_FOUND = "No board found!";

    public LeaderboardService(LeaderboardRepository leaderboardRepository, StringRedisTemplate stringRedisTemplate,
                              RedisScript<Void> script) {
        this.leaderboardRepository = leaderboardRepository;
        this.stringRedisTemplate = stringRedisTemplate;
        this.script = script;
    }

    public String createBoard(int topN) {
        Leaderboard leaderboard = new Leaderboard(topN);
        leaderboardRepository.save(leaderboard);
        return leaderboard.getId();
    }

    public void deleteBoard(String boardKey) {
        stringRedisTemplate.delete(SCORES_SET_PREFIX + boardKey);
        leaderboardRepository.deleteById(boardKey);
    }

    public LeaderboardDto readBoard(String boardKey) {
        Optional<Leaderboard> boardOpt = leaderboardRepository.findById(boardKey);
        if (boardOpt.isEmpty()) {
            throw new IllegalArgumentException(BOARD_NOT_FOUND);
        }
        Leaderboard board = boardOpt.get();
        List<Score> scores = getScores(boardKey);
        return new LeaderboardDto(board.getTopN(), scores);
    }

    public void updateBoard(String boardKey, LeaderboardChangeRequest request) {
        Optional<Leaderboard> boardOpt = leaderboardRepository.findById(boardKey);
        if (boardOpt.isEmpty()) {
            throw new IllegalArgumentException(BOARD_NOT_FOUND);
        }
        Leaderboard board = boardOpt.get();
        board.setTopN(request.getTopN());
        leaderboardRepository.save(board);
    }

    public void clearScores(String boardKey) {
        Optional<Leaderboard> boardOpt = leaderboardRepository.findById(boardKey);
        if (boardOpt.isEmpty()) {
            throw new IllegalArgumentException(BOARD_NOT_FOUND);
        }
        stringRedisTemplate.delete(SCORES_SET_PREFIX + boardKey);
    }

    public void createScore(String boardKey, Score score) {
        Optional<Leaderboard> boardOpt = leaderboardRepository.findById(boardKey);
        if (boardOpt.isEmpty()) {
            throw new IllegalArgumentException(BOARD_NOT_FOUND);
        }
        Leaderboard board = boardOpt.get();
        int topN = board.getTopN();
        UUID uuid = UUID.randomUUID();

        stringRedisTemplate.execute(script, List.of(SCORES_SET_PREFIX + boardKey),
                score.getName() + "|SEP|" + uuid.toString(), String.valueOf(score.getValue()),
                String.valueOf(topN));
    }

    public List<Score> getScores(String boardKey) {
        Set<ZSetOperations.TypedTuple<String>> actualScores =
                stringRedisTemplate.boundZSetOps(SCORES_SET_PREFIX + boardKey).reverseRangeWithScores(0, -1);
        if (actualScores == null) {
            return Collections.emptyList();
        }
        return actualScores.stream().map(tuple -> {
            String nameWithUuid = tuple.getValue();
            String nameWithoutUuid = nameWithUuid == null ? "" : nameWithUuid.split("\\|SEP\\|")[0];
            return new Score(nameWithoutUuid, Objects.requireNonNull(tuple.getScore()).intValue());
        }).collect(Collectors.toList());
    }

    public boolean scoreEligibleForBoardMembership(String boardKey, int score) {
        Set<ZSetOperations.TypedTuple<String>> actualScores =
                stringRedisTemplate.boundZSetOps(SCORES_SET_PREFIX + boardKey).reverseRangeWithScores(0, -1);
        if (actualScores == null) {
            return true;
        }
        long lessThanScoreCount = actualScores.stream()
                .map(ZSetOperations.TypedTuple::getScore)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .filter(doubleValue -> (int) doubleValue < score)
                .count();
        return lessThanScoreCount > 0;
    }
}
