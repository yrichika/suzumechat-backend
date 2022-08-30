package com.example.suzumechat.service.guest;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.suzumechat.service.channel.Channel;
import com.example.suzumechat.service.channel.ChannelRepository;
import com.example.suzumechat.service.guest.dto.ChannelStatus;
import com.example.suzumechat.service.guest.dto.VisitorsRequest;
import com.example.suzumechat.service.guest.exception.JoinChannelTokenInvalidException;
import com.example.suzumechat.utility.*;

import lombok.val;

@Service
public class GuestServiceImpl implements GuestService {

    @Autowired
    private GuestRepository repository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private Hash hash;
    @Autowired
    private Crypter crypter;


    @Override
    public ChannelStatus getChannelNameByJoinChannelToken(String joinChannelToken) throws Exception {
        final Channel channel = getChannelByJoinChannelToken(joinChannelToken);

        val channelName = crypter.decrypt(channel.getChannelNameEnc(), channel.getChannelId());

        if (channel.getSecretKeyEnc() == null) {
            return new ChannelStatus(channelName, false);
        }

        return new ChannelStatus(channelName, true);
    }

    @Override
    public Optional<String> createGuestAsVisitor(
        final String joinChannelToken,
        final String codename,
        final String passphrase
    ) throws Exception {
        
        final Channel channel = getChannelByJoinChannelToken(joinChannelToken);
        if (channel.secretKeyEmpty()) {
            return Optional.ofNullable(null);
        }
        
        val visitorId = UUID.randomUUID().toString();
        val visitorIdHashed = hash.digest(visitorId);
        val visitorIdEnc = crypter.encrypt(visitorId, channel.getChannelId());
        val codenameEnc = crypter.encrypt(codename, channel.getChannelId());
        val passphraseEnc = crypter.encrypt(passphrase, channel.getChannelId());

        Guest visitor = new Guest();
        visitor.setChannelId(channel.getChannelId());
        visitor.setVisitorIdEnc(visitorIdEnc);
        visitor.setVisitorIdHashed(visitorIdHashed);
        visitor.setCodenameEnc(codenameEnc);
        visitor.setIsAuthenticated(null);
        visitor.setPassphraseEnc(passphraseEnc);

        repository.save(visitor);

        return Optional.of(visitorId);
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


    public Channel getChannelByJoinChannelToken(final String joinChannelToken) throws Exception {
        val joinChannelTokenHashed = hash.digest(joinChannelToken);
        final Channel channel = channelRepository.findByJoinChannelTokenHashed(joinChannelTokenHashed);

        if (channel == null) {
            throw new JoinChannelTokenInvalidException();
        }
        return channel;
    }
}
