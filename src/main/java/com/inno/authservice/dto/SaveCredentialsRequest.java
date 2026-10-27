package com.inno.authservice.dto;

import com.inno.authservice.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SaveCredentialsRequest {

    @NonNull
    private Long userId;

    @NotBlank
    @Size(min = 5, max = 100)
    private String login;

    @NotBlank
    @Size(min = 8, max = 72)
    private String password;

    @NonNull
    private Role role;
}
