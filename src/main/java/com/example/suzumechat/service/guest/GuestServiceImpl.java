package com.example.suzumechat.service.guest;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.suzumechat.service.guest.dto.VisitorRequest;
import com.example.suzumechat.utility.*;

import lombok.val;

@Service
public class GuestServiceImpl implements GuestService {

    @Autowired
    private GuestRepository repository;
    @Autowired
    private Hash hash;
    @Autowired
    private Crypter crypter;

    @Override
    public VisitorRequest createGuestAsVisitor(String codename, String passphrase, String channelId) throws Exception {
        val visitorId = UUID.randomUUID().toString();
        val visitorIdHashed = hash.digest(visitorId);
        val visitorIdEnc = crypter.encrypt(visitorId, channelId);
        val codenameEnc = crypter.encrypt(codename, channelId);
        val passphraseEnc = crypter.encrypt(passphrase, channelId);

        Guest visitor = new Guest();
        visitor.setChannelId(channelId);
        visitor.setVisitorIdEnc(visitorIdEnc);
        visitor.setVisitorIdHashed(visitorIdHashed);
        visitor.setCodenameEnc(codenameEnc);
        visitor.setIsAuthenticated(null);
        visitor.setPassphraseEnc(passphraseEnc);

        repository.save(visitor);

        return new VisitorRequest(visitorId, codename, passphrase, Optional.ofNullable(null));
    }

    @Override
    public void promoteToGuest(String channelId, String visitorId) throws Exception {

        val guestId = UUID.randomUUID().toString();
        val guestIdHashed = hash.digest(guestId);
        val guestIdEnc = crypter.encrypt(guestId, channelId);

        val visitorIdHashed = hash.digest(visitorId);
        val guest = repository.findByVisitorIdHashed(visitorIdHashed);

        guest.setGuestIdHashed(guestIdHashed);
        guest.setGuestIdEnc(guestIdEnc);
        guest.setIsAuthenticated(true);

        repository.save(guest);
    }

    @Override
    public void updateStatus(String visitorId, Boolean isAuthenticated) {
        val visitorIdHashed = hash.digest(visitorId);
        repository.updateIsAuthenticatedByVisitorIdHashed(visitorIdHashed, isAuthenticated);
    }
}
