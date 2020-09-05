package engine;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

public class MyUser {
    @Email(message = "Email should be valid", regexp=".+@.+\\..+")
    private String email;
    @Size(message = "The password must have at least five characters", min = 5)
    private String password;
    private String roles = "USER";

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
