package com.example.suzumechat.service.guest.form;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.*;
import com.example.suzumechat.utility.form.*;
import lombok.*;

@Data
public class JoinRequest {
    @NotBlank(groups = ValidationGroup1.class)
    @Length(min = 3, max = 64, groups = ValidationGroup2.class)
    private String codename;

    @NotBlank(groups = ValidationGroup1.class)
    @Length(min = 3, max = 128, groups = ValidationGroup2.class)
    private String passphrase;
}
