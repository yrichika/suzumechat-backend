package com.example.suzumechat.service.guest;

import com.example.suzumechat.service.guest.dto.ChannelStatus;
import com.example.suzumechat.service.guest.dto.VisitorsRequest;

public interface GuestService {
    
    public ChannelStatus getChannelNameByJoinChannelToken(String joinChannelToken) throws Exception;

    // originally ClientLoginRequests.create()
    // 使うときは、JoinRequestのフォームの値をここに渡すこと
    public VisitorsRequest createGuestAsVisitor(String codename, String passphrase, String channelId) throws Exception;

    // originally AuthenticatedClients.create()
    public void promoteToGuest(String channelId, String visitorId) throws Exception;

    // originally ClientLoginRequests.updateStatus
    public void updateStatus(String visitorId, Boolean isAuthenticated);
}
