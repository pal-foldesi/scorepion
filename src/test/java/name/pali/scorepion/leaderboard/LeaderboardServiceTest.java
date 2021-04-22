package name.pali.scorepion.leaderboard;

import name.pali.scorepion.auth.jpa.AuthorityRepository;
import name.pali.scorepion.auth.jpa.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LeaderboardServiceTest {
    private static final DockerImageName REDIS_IMAGE = DockerImageName.parse("redis:latest");
    private static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(REDIS_IMAGE)
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        REDIS_CONTAINER.start();
        registry.add("spring.redis.host", REDIS_CONTAINER::getContainerIpAddress);
        registry.add("spring.redis.port", REDIS_CONTAINER::getFirstMappedPort);
    }

    @Autowired
    LeaderboardRepository leaderboardRepository;

    @Autowired
    private LeaderboardService leaderboardService;

    // JPA-related unneeded beans
    @MockBean
    private DataSource dataSource;

    @MockBean
    private EntityManagerFactory entityManagerFactory;

    @MockBean
    private CommandLineRunner firstAdminCreatingRunner;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AuthorityRepository authorityRepository;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    void clearDatastoreBefore() {
        clearDatastore();
    }

    @AfterEach
    void clearDatastoreAfter() {
        clearDatastore();
    }

    void clearDatastore() {
        for (Leaderboard board : leaderboardRepository.findAll()) {
            String boardKey = board.getId();
            leaderboardService.deleteBoard(boardKey);
        }
    }

    @Test
    void canCreateLeaderboard() {
        String boardKey = leaderboardService.createBoard(3);
        assertThat(boardKey).isNotNull();

        Optional<Leaderboard> created = leaderboardRepository.findById(boardKey);
        assertThat(created).isPresent();
    }

    @Test
    void canDeleteLeaderboard() {
        String boardKey = leaderboardService.createBoard(3);

        leaderboardService.deleteBoard(boardKey);

        Optional<Leaderboard> deleted = leaderboardRepository.findById(boardKey);
        assertThat(deleted).isEmpty();
    }

    @Test
    void canReadLeaderboard() {
        int topN = 3;

        String boardKey = leaderboardService.createBoard(topN);

        Score[] scoresToCreate = {
                new Score("Alphonso", 3),
                new Score("Derek", 2),
                new Score("Lyssa", 5)
        };

        for (Score score : scoresToCreate) {
            leaderboardService.createScore(boardKey, score);
        }

        LeaderboardDto boardDto = leaderboardService.readBoard(boardKey);

        int actualTopN = boardDto.getTopN();

        List<Score> scores = boardDto.getScores();

        assertThat(actualTopN).isEqualTo(topN);

        Score[] expectedScores = {
                new Score("Lyssa", 5),
                new Score("Alphonso", 3),
                new Score("Derek", 2)
        };

        assertThat(scores)
                .isNotNull()
                .containsExactly(expectedScores);
    }

    @Test
    void canUpdateLeaderboard() {
        int originalTopN = 3;

        String boardKey = leaderboardService.createBoard(originalTopN);

        LeaderboardChangeRequest request = new LeaderboardChangeRequest();

        int newTopN = 5;

        request.setTopN(newTopN);

        leaderboardService.updateBoard(boardKey, request);

        Optional<Leaderboard> boardOpt = leaderboardRepository.findById(boardKey);

        assertThat(boardOpt).isPresent();

        Leaderboard board = boardOpt.get();

        int actualTopN = board.getTopN();

        assertThat(actualTopN).isEqualTo(newTopN);
    }

    @Test
    void canClearScores() {
        int topN = 3;

        String boardKey = leaderboardService.createBoard(topN);

        Score[] scoresToCreate = {
                new Score("Alphonso", 3),
                new Score("Derek", 2),
                new Score("Lyssa", 5)
        };

        for (Score score : scoresToCreate) {
            leaderboardService.createScore(boardKey, score);
        }

        leaderboardService.clearScores(boardKey);

        List<Score> scores = leaderboardService.getScores(boardKey);

        assertThat(scores).isEmpty();
    }

    private static Stream<Arguments> canCheckIfScoreIsEligibleForSubmissionArgumentProvider() {
        return Stream.of(
                Arguments.of(1, false),
                Arguments.of(2, false),
                Arguments.of(3, true),
                Arguments.of(4, true),
                Arguments.of(5, true),
                Arguments.of(6, true)
        );
    }

    @ParameterizedTest
    @MethodSource("canCheckIfScoreIsEligibleForSubmissionArgumentProvider")
    void canCheckIfScoreIsEligibleForSubmission(int obtainedScore, boolean expectedResult) {
        int topN = 3;

        String boardKey = leaderboardService.createBoard(topN);

        Score[] scoresToCreate = {
                new Score("Alphonso", 3),
                new Score("Derek", 2),
                new Score("Lyssa", 5)
        };

        for (Score score : scoresToCreate) {
            leaderboardService.createScore(boardKey, score);
        }

        boolean actualResult = leaderboardService.scoreEligibleForBoardMembership(boardKey, obtainedScore);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void canRetrieveScores() {
        int topN = 10;

        String boardKey = leaderboardService.createBoard(topN);

        Score[] scores = {
                new Score("Naomi", 12),
                new Score("Peter", 12),
                new Score("Naomi", 12),
                new Score("Peter", 12),
                new Score("Tessa", 33),
                new Score("Michael", 123),
                new Score("Michael", 123),
                new Score("Michael", 123),
                new Score("Evgeny", 53),
                new Score("Evgeny", 53),
                new Score("Evgeny", 232),
                new Score("Li", 179),
                new Score("Li", 179),
                new Score("Li", 341),
                new Score("Julius", 76),
                new Score("Olga", 76),
                new Score("Chris", 76),
                new Score("Louis", 76),
                new Score("Louis XIV", 87),
                new Score("Pavel", 54),
                new Score("Sven", 99),
                new Score("Sebastian", 78),
                new Score("Johanna", 12),
                new Score("Fido", 243),
                new Score("Susan", 243),
                new Score("John Smith", 112),
                new Score("Donald", 114),
                new Score("Sarah", 243),
                new Score("Sarah", 243),
                new Score("Arnold", 243),
        };

        for (Score score : scores) {
            leaderboardService.createScore(boardKey, score);
        }

        Optional<Leaderboard> created = leaderboardRepository.findById(boardKey);
        assertThat(created).isPresent();

        List<Score> actualScoresWithNames = leaderboardService.getScores(boardKey);

        List<Score> expectedScores = List.of(
                new Score("Li", 341),
                new Score("Susan", 243),
                new Score("Sarah", 243),
                new Score("Sarah", 243),
                new Score("Fido", 243),
                new Score("Arnold", 243),
                new Score("Evgeny", 232),
                new Score("Li", 179),
                new Score("Li", 179),
                new Score("Michael", 123));
        assertThat(actualScoresWithNames).usingRecursiveComparison().isEqualTo(expectedScores);
    }

    @Test
    void canRetrieveScoresAfterSubmissionUsingSeveralThreads() throws InterruptedException {
        int topN = 10;
        String boardKey = leaderboardService.createBoard(topN);

        Thread t1 = new Thread(new ScoreSubmitter(boardKey, leaderboardService));
        Thread t2 = new Thread(new ScoreSubmitter(boardKey, leaderboardService));
        Thread t3 = new Thread(new ScoreSubmitter(boardKey, leaderboardService));

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        Optional<Leaderboard> created = leaderboardRepository.findById(boardKey);
        assertThat(created).isPresent();

        List<Score> actualScoresWithNames = leaderboardService.getScores(boardKey);

        List<Score> expectedScores = List.of(
                new Score("Name 499", 499),
                new Score("Name 499", 499),
                new Score("Name 499", 499),
                new Score("Name 498", 498),
                new Score("Name 498", 498),
                new Score("Name 498", 498),
                new Score("Name 497", 497),
                new Score("Name 497", 497),
                new Score("Name 497", 497),
                new Score("Name 496", 496));
        assertThat(actualScoresWithNames).usingRecursiveComparison().isEqualTo(expectedScores);
    }

    private static class ScoreSubmitter implements Runnable {
        private final String boardKey;
        private final LeaderboardService leaderboardService;

        ScoreSubmitter(String boardKey, LeaderboardService service) {
            this.boardKey = boardKey;
            this.leaderboardService = service;
        }

        @Override
        public void run() {
            for (int i = 0; i < 500; i++) {
                Score score = new Score("Name " + i, i);
                leaderboardService.createScore(boardKey, score);
            }
        }
    }
}
