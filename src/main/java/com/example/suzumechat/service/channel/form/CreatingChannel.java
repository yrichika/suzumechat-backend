package com.example.suzumechat.service.channel.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;
import com.example.suzumechat.utility.form.ValidationGroup1;
import com.example.suzumechat.utility.form.ValidationGroup2;
import lombok.Data;

@Data
public class CreatingChannel {
    @NotBlank(groups = ValidationGroup1.class)
    @Length(min = 1, max = 100, groups = ValidationGroup2.class)
    private String channelName;

    @NotNull(groups = ValidationGroup1.class)
    @Size(min = 30, max = 50)
    private String publicKey;
}
