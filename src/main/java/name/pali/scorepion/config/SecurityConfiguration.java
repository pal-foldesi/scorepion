package name.pali.scorepion.config;

import name.pali.scorepion.auth.jwt.JwtConfigurer;
import name.pali.scorepion.auth.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletResponse;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfiguration(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, ex) -> response.sendError(
                                HttpServletResponse.SC_UNAUTHORIZED,
                                ex.getMessage()
                        )
                )
                .and()
                .authorizeRequests()
                // Our public endpoints
                .antMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll()
                .antMatchers(HttpMethod.GET, "/swagger-ui.html").permitAll()
                .antMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
                .antMatchers(HttpMethod.GET, "/").permitAll()
                .antMatchers("/auth/login").permitAll()
                .antMatchers(HttpMethod.GET, "/boards/{boardKey}").permitAll()
                .antMatchers(HttpMethod.GET, "/boards/{boardKey}/scores").permitAll()
                .antMatchers(HttpMethod.POST, "/boards/{boardKey}/scores").permitAll()
                .antMatchers(HttpMethod.GET, "/boards/{boardKey}/eligibility").permitAll()
                // Our private endpoints
                .anyRequest().authenticated()
                .and()
                .apply(securityConfigurerAdapter());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private JwtConfigurer securityConfigurerAdapter() {
        return new JwtConfigurer(jwtTokenProvider);
    }
}