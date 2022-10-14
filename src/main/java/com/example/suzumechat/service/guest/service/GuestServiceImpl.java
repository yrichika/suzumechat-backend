package com.example.suzumechat.service.guest.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.suzumechat.service.channel.Channel;
import com.example.suzumechat.service.channel.ChannelRepository;
import com.example.suzumechat.service.guest.Guest;
import com.example.suzumechat.service.guest.GuestRepository;
import com.example.suzumechat.service.guest.dto.ChannelStatus;
import com.example.suzumechat.service.guest.dto.message.AuthenticationStatus;
import com.example.suzumechat.service.guest.dto.message.VisitorsRequest;
import com.example.suzumechat.service.guest.exception.GuestNotFoundException;
import com.example.suzumechat.service.guest.exception.JoinChannelTokenInvalidException;
import com.example.suzumechat.service.guest.exception.VisitorInvalidException;
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
    public ChannelStatus getChannelNameByJoinChannelToken(String joinChannelToken)
            throws Exception {
        final Channel channel = getChannelByJoinChannelToken(joinChannelToken);

        val channelName =
                crypter.decrypt(channel.getChannelNameEnc(), channel.getChannelId());

        if (channel.getSecretKeyEnc() == null) {
            return new ChannelStatus(channelName, false);
        }

        return new ChannelStatus(channelName, true);
    }

    @Override
    public Guest getByGuestId(final String guestId) throws Exception {
        val guestIdHashed = hash.digest(guestId);
        val guestOpt = repository.findByGuestIdHashed(guestIdHashed);
        val guest = guestOpt.orElseThrow(GuestNotFoundException::new);

        return guest;
    }

    @Override
    public Optional<String> createGuestAsVisitor(final String joinChannelToken,
            final String visitorId, final String codename, final String passphrase)
            throws Exception {

        final Channel channel = getChannelByJoinChannelToken(joinChannelToken);
        if (channel.secretKeyEmpty()) {
            return Optional.empty();
        }

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
    public void updateStatus(String visitorId, Boolean isAuthenticated) {
        val visitorIdHashed = hash.digest(visitorId);
        repository.updateIsAuthenticatedByVisitorIdHashed(visitorIdHashed,
                isAuthenticated);
    }

    // FIXME: move to another appropriate class
    public Channel getChannelByJoinChannelToken(final String joinChannelToken)
            throws Exception {
        val joinChannelTokenHashed = hash.digest(joinChannelToken);
        final Optional<Channel> channelOpt = channelRepository
                .findByJoinChannelTokenHashed(joinChannelTokenHashed);
        val channel = channelOpt.orElseThrow(JoinChannelTokenInvalidException::new);

        return channel;
    }
}
