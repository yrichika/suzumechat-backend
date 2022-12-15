package com.example.suzumechat.service.valueobject;

import com.example.suzumechat.service.valueobject.type.VisitorHandlingStringType;

public record EmptyChannelToken() implements VisitorHandlingStringType {

    public String value() {
        return "";
    }
}
