package name.pali.scorepion.leaderboard;

import name.pali.scorepion.ScorepionApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ScorepionApplication.class)
@AutoConfigureMockMvc
@PropertySource("classpath:application.properties")
class AuthApiTest {
    private static final String POSTGRES_PASSWORD = "dbTestPass";
    private static final DockerImageName SQL_IMAGE = DockerImageName.parse("postgres:13.2");
    private static final GenericContainer<?> SQL_CONTAINER = new GenericContainer<>(SQL_IMAGE)
            .withExposedPorts(5432)
            .withEnv("POSTGRES_PASSWORD", POSTGRES_PASSWORD);

    @DynamicPropertySource
    static void sqlProperties(DynamicPropertyRegistry registry) {
        SQL_CONTAINER.start();
        String hostname = SQL_CONTAINER.getContainerIpAddress();
        int port = SQL_CONTAINER.getFirstMappedPort();

        registry.add("spring.datasource.url", () -> "jdbc:postgresql://" + hostname + ":" + port + "/postgres");
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> POSTGRES_PASSWORD);
    }

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @DisplayName("Can login as existing user")
    @Test
    void givenMockUserThatIsAdmin_whenLeaderboardCreationRequested_then200OkIsReturned() throws Exception {
        MvcResult mvcResult = mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"" + adminUsername + "\",\"password\": \"" + adminPassword + "\"}")
        ).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);

        String jwtToken = mvcResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        assertThat(jwtToken).isNotNull();
    }

    @DisplayName("Can't login as nonexistent user")
    @Test
    void givenMockUserThatIsNotAdmin_whenLeaderboardCreationRequested_then403ForbiddenIsReturned() throws Exception {
        mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"beware\",\"password\": \"ofrequestsbearinggifts\"}")
        ).andExpect(status().isUnauthorized());
    }
}