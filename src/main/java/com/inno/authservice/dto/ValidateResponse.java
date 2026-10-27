package com.inno.authservice.dto;

import com.inno.authservice.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidateResponse {

    private boolean valid;
    private Long userId;
    private Role role;
}
