package name.pali.scorepion.leaderboard;

import name.pali.scorepion.ScorepionApplication;
import name.pali.scorepion.auth.jpa.AuthorityRepository;
import name.pali.scorepion.auth.jpa.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ScorepionApplication.class)
@AutoConfigureMockMvc
class LeaderboardControllerTest {
    @MockBean
    private LeaderboardService leaderboardService;

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

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
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @DisplayName("Can create a leaderboard as an admin")
    @WithMockUser(roles = "ADMIN")
    @Test
    void givenMockUserThatIsAdmin_whenLeaderboardCreationRequested_then200OkIsReturned() throws Exception {
        mvc.perform(post("/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"topN\": \"10\"}")
        ).andExpect(status().isOk());
    }

    @DisplayName("Can't create a leaderboard as a guest")
    @Test
    void givenMockUserThatIsNotAdmin_whenLeaderboardCreationRequested_then403ForbiddenIsReturned() throws Exception {
        mvc.perform(post("/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"topN\": \"10\"}")
        ).andExpect(status().isUnauthorized());
    }

    @DisplayName("Can get a leaderboard as an admin")
    @WithMockUser(roles = "ADMIN")
    @Test
    void givenMockUserThatIsAdmin_whenGettingLeaderboard_then200OkIsReturned() throws Exception {
        mvc.perform(get("/boards/{boardKey}", "whatever")
        ).andExpect(status().isOk());
    }

    @DisplayName("Can get a leaderboard as a guest")
    @Test
    void givenNoUser_whenGettingLeaderboard_then200OkIsReturned() throws Exception {
        mvc.perform(get("/boards/{boardKey}", "whatever")
        ).andExpect(status().isOk());
    }

    @DisplayName("Can get scores of a leaderboard as an admin")
    @WithMockUser(roles = "ADMIN")
    @Test
    void givenMockUserThatIsAdmin_whenGettingScoresOfLeaderboard_then200OkIsReturned() throws Exception {
        mvc.perform(get("/boards/{boardKey}/scores", "whatever")
        ).andExpect(status().isOk());
    }

    @DisplayName("Can get scores of a leaderboard as a guest")
    @Test
    void givenNoUser_whenGettingScoresOfLeaderboard_then200OkIsReturned() throws Exception {
        mvc.perform(get("/boards/{boardKey}/scores", "whatever")
        ).andExpect(status().isOk());
    }

    @DisplayName("Can update a leaderboard as an admin")
    @WithMockUser(roles = "ADMIN")
    @Test
    void givenMockUserThatIsAdmin_whenUpdatingLeaderboard_then200OkIsReturned() throws Exception {
        mvc.perform(put("/boards/{boardKey}", "whatever")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"topN\": \"10\"}")
        ).andExpect(status().isOk());
    }

    @DisplayName("Can't update a leaderboard as a guest")
    @Test
    void givenNoUser_whenUpdatingLeaderboard_then200OkIsReturned() throws Exception {
        mvc.perform(put("/boards/{boardKey}", "whatever")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"topN\": \"10\"}")
        ).andExpect(status().isUnauthorized());
    }

    @DisplayName("Can clear scores of a leaderboard as an admin")
    @WithMockUser(roles = "ADMIN")
    @Test
    void givenMockUserThatIsAdmin_whenClearingScoresOfLeaderboard_then200OkIsReturned() throws Exception {
        mvc.perform(post("/boards/{boardKey}/scores/clear", "whatever")
        ).andExpect(status().isOk());
    }

    @DisplayName("Can't clear scores of a leaderboard as a guest")
    @Test
    void givenGuest_whenClearingScoresOfLeaderboard_then401UnauthorizedIsReturned() throws Exception {
        mvc.perform(post("/boards/{boardKey}/scores/clear", "whatever")
        ).andExpect(status().isUnauthorized());
    }

    @DisplayName("Can check whether score is eligible for leaderboard membership as an admin")
    @WithMockUser(roles = "ADMIN")
    @Test
    void givenMockUserThatIsAdmin_whenCheckingScoreLeaderboardEligibility_then200OkIsReturned() throws Exception {
        mvc.perform(get("/boards/{boardKey}/eligibility", "whatever").param("score", String.valueOf(5))
        ).andExpect(status().isOk());
    }

    @DisplayName("Can check whether score is eligible for leaderboard membership as a guest")
    @Test
    void givenGuest_whenCheckingScoreLeaderboardEligibility_then200OkIsReturned() throws Exception {
        mvc.perform(get("/boards/{boardKey}/eligibility", "whatever").param("score", String.valueOf(5))
        ).andExpect(status().isOk());
    }

    @DisplayName("Can submit a score to a leaderboard as an admin")
    @WithMockUser(roles = "ADMIN")
    @Test
    void givenMockUserThatIsAdmin_whenSubmittingScoreToLeaderboard_then200OkIsReturned() throws Exception {
        mvc.perform(post("/boards/{boardKey}/scores", "whatever")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"goodplayer\",\"score\": \"42\"}")
        ).andExpect(status().isOk());
    }

    @DisplayName("Can submit a score to a leaderboard as a guest")
    @Test
    void givenGuest_whenSubmittingScoreToLeaderboard_then200OkIsReturned() throws Exception {
        mvc.perform(post("/boards/{boardKey}/scores", "whatever")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"goodplayer\",\"score\": \"42\"}")
        ).andExpect(status().isOk());
    }

    @DisplayName("Can delete a leaderboard as an admin")
    @WithMockUser(roles = "ADMIN")
    @Test
    void givenMockUserThatIsAdmin_whenDeletingLeaderboard_then200OkIsReturned() throws Exception {
        mvc.perform(delete("/boards/{boardKey}", "whatever")
        ).andExpect(status().isOk());
    }

    @DisplayName("Can't delete a leaderboard as a guest")
    @Test
    void givenGuest_whenDeletingLeaderboard_then401UnauthorizedIsReturned() throws Exception {
        mvc.perform(delete("/boards/{boardKey}", "whatever")
        ).andExpect(status().isUnauthorized());
    }
}