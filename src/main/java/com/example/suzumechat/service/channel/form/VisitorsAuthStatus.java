package com.example.suzumechat.service.channel.form;

import javax.validation.constraints.*;

import com.example.suzumechat.utility.form.*;

import lombok.*;

@Data
public class VisitorsAuthStatus {
    @NotBlank(groups = ValidationGroup1.class)
    private String visitorId;

    @NotNull
    private Boolean isAuthenticated;
}
