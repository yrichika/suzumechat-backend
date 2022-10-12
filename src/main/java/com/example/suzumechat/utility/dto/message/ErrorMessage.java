package com.example.suzumechat.utility.dto.message;

public interface ErrorMessage {
    /**
     * this method is used only by frontend to identify the message is an error
     * message. `getIs...` is used because when parsed to JSON, `get` part will be
     * deleted.
     */
    public default boolean getIsError() {
        return true;
    }

    public default String getType() {
        return this.getClass().getSimpleName();
    }
}
