package com.example.suzumechat.service.guest;

import com.example.suzumechat.service.guest.dto.VisitorRequest;

public interface GuestService {
    
    // originally ClientLoginRequests.create()
    // 使うときは、JoinRequestのフォームの値をここに渡すこと
    public VisitorRequest createGuestAsVisitor(String codename, String passphrase, String channelId) throws Exception;

    // originally AuthenticatedClients.create()
    public void promoteToGuest(String channelId, String visitorId) throws Exception;


    // originally ClientLoginRequests.updateStatus
    public void updateStatus(String visitorId, Boolean isAuthenticated);
}
