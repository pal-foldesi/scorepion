package name.pali.scorepion;

import name.pali.scorepion.auth.jpa.Authority;
import name.pali.scorepion.auth.jpa.AuthorityRepository;
import name.pali.scorepion.auth.jpa.User;
import name.pali.scorepion.auth.jpa.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@PropertySource("classpath:application.properties")
@Component
public class FirstAdminCreatingRunner implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(FirstAdminCreatingRunner.class);

    @Value("${admin.username:#{null}}")
    private String adminUsername;

    @Value("${admin.password:#{null}}")
    private String adminPassword;

    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;

    public FirstAdminCreatingRunner(PasswordEncoder passwordEncoder, UserRepository userRepository,
                                    AuthorityRepository authorityRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    @Override
    public void run(String... args) {
        if (adminUsername != null && adminPassword != null && !"".equals(adminUsername) && !"".equals(adminPassword)) {
            Authority adminAuthority = new Authority();
            adminAuthority.setName("ROLE_ADMIN");
            Optional<User> existingAdminOpt = userRepository.findOneByAuthorities(adminAuthority);
            if (existingAdminOpt.isEmpty()) {
                Optional<Authority> existingAuthorityOpt = authorityRepository.findByName("ROLE_ADMIN");
                if (existingAuthorityOpt.isEmpty()) {
                    authorityRepository.save(adminAuthority);
                }
                User firstAdmin = new User();
                firstAdmin.setUsername(adminUsername);
                firstAdmin.setPassword(passwordEncoder.encode(adminPassword));
                firstAdmin.setAuthorities(Set.of(adminAuthority));
                userRepository.save(firstAdmin);
                logger.info("First admin saved with username {}", adminUsername);
            } else {
                logger.info("Existing admin detected");
            }
        } else {
            logger.warn("First admin username and/or password not provided");
        }
    }
}
