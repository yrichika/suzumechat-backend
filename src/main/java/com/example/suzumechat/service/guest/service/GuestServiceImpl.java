package com.example.suzumechat.service.guest.service;

import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.suzumechat.service.channel.Channel;
import com.example.suzumechat.service.channel.exception.VisitorNotFoundException;
import com.example.suzumechat.service.guest.Guest;
import com.example.suzumechat.service.guest.GuestRepository;
import com.example.suzumechat.service.guest.exception.GuestNotFoundException;
import com.example.suzumechat.utility.Crypter;
import com.example.suzumechat.utility.Hash;
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
    public Guest getByGuestId(final String guestId) throws Exception {
        val guestIdHashed = hash.digest(guestId);
        val guestOpt = repository.findByGuestIdHashed(guestIdHashed);
        val guest = guestOpt.orElseThrow(GuestNotFoundException::new);

        return guest;
    }

    @Override
    public Optional<String> createGuestAsVisitor(final String joinChannelToken,
        final String visitorId, Channel channel)
        throws Exception {

        if (channel.secretKeyEmpty()) {
            return Optional.empty();
        }

        val visitorIdHashed = hash.digest(visitorId);
        val visitorIdEnc = crypter.encrypt(visitorId, channel.getChannelId());

        Guest visitor = new Guest();
        visitor.setChannelId(channel.getChannelId());
        visitor.setVisitorIdEnc(visitorIdEnc);
        visitor.setVisitorIdHashed(visitorIdHashed);
        visitor.setIsAuthenticated(null);

        repository.save(visitor);

        return Optional.of(visitorId);
    }

    @Override
    public void updateStatus(String visitorId, Boolean isAuthenticated) {
        val visitorIdHashed = hash.digest(visitorId);
        repository.updateIsAuthenticatedByVisitorIdHashed(visitorIdHashed,
            isAuthenticated);
    }

    @Override
    public Guest approveVisitor(String visitorId, boolean isAuthenticated)
        throws Exception {
        val visitorIdHashed = hash.digest(visitorId);
        final Optional<Guest> guestOpt =
            repository.findByVisitorIdHashed(visitorIdHashed);
        val guest = guestOpt.orElseThrow(VisitorNotFoundException::new);

        if (!isAuthenticated) {
            guest.setIsAuthenticated(false);
            repository.save(guest);
            return guest;
        }

        val guestId = UUID.randomUUID().toString();
        val guestIdHashed = hash.digest(guestId);
        val guestIdEnc = crypter.encrypt(guestId, guest.getChannelId());

        guest.setGuestIdHashed(guestIdHashed);
        guest.setGuestIdEnc(guestIdEnc);
        guest.setIsAuthenticated(true);

        repository.save(guest);

        return guest;
    }
}
