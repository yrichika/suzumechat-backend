package com.example.suzumechat.service.valueobject;

import com.example.suzumechat.service.valueobject.type.VisitorHandlingStringType;

public record ChannelToken(String value) implements VisitorHandlingStringType {

}
