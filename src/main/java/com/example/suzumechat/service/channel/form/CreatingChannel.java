package com.example.suzumechat.service.channel.form;

import javax.validation.constraints.*;

import org.hibernate.validator.constraints.Length;

import com.example.suzumechat.utility.form.*;

import lombok.*;

@Data
public class CreatingChannel {
    @NotBlank(groups = ValidationGroup1.class)
    @Length(min = 1, max = 100, groups = ValidationGroup2.class)
    private String channelName;
}
