package mg.mherifaniry.Tickets.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
    private int id;
    @NotBlank(message = "Username is mandatory")
    private String username;
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;
    @NotBlank(message = "Password is mandatory")
    private String password;

    public UserDto(int id, String username, String email)
    {
        this.id = id;
        this.username = username;
        this.email = email;
    }
}
