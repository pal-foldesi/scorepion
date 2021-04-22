package name.pali.scorepion;

import name.pali.scorepion.auth.jpa.AuthorityRepository;
import name.pali.scorepion.auth.jpa.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@SpringBootTest
class ScorepionApplicationTests {
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

	@Test
	void contextLoads() {
	}

}
