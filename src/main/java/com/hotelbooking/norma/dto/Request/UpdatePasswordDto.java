package com.hotelbooking.norma.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data

public class UpdatePasswordDto {
    @NotBlank
    private String userCode;
    
    @Size(min = 5, max = 10, message = "The length of the password should be between 5 and 10")
    private String oldPassword;

    @Size(min = 5, max = 10, message = "The length of the password should be between 5 and 10")
    private String newPassword;
}
