package name.pali.scorepion.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import name.pali.scorepion.auth.jwt.JwtTokenProvider;
import name.pali.scorepion.config.CorsAllowedOrigins;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Authentication")
@RestController
@RequestMapping
public class AuthApi {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthApi(AuthenticationManager authenticationManager,
                   JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @CrossOrigin(origins = CorsAllowedOrigins.LOCAL, methods = RequestMethod.POST)
    @PostMapping("/auth/login")
    public ResponseEntity<Object> login(@RequestBody @Valid AuthRequest request) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getUsername(), request.getPassword()
                            )
                    );
            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            jwtTokenProvider.generateAccessToken(authentication)
                    ).build();
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}