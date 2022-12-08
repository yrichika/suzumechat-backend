package com.example.suzumechat.service.channel.form;

import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import com.example.suzumechat.utility.form.ValidationGroup1;
import com.example.suzumechat.utility.form.ValidationGroup2;
import lombok.Data;

@Data
public class CreatingChannel {
    @NotBlank(groups = ValidationGroup1.class)
    @Length(min = 1, max = 100, groups = ValidationGroup2.class)
    private String channelName;

    @NotBlank(groups = ValidationGroup1.class)
    @Length(min = 30, max = 50, groups = ValidationGroup2.class)
    private String publicKey;
}
