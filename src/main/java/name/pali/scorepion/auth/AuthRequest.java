package name.pali.scorepion.auth;

import javax.validation.constraints.NotNull;

public class AuthRequest {

    @NotNull
    private String username;
    @NotNull
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}