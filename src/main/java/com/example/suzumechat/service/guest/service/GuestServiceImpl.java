package com.example.suzumechat.service.guest.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.suzumechat.service.channel.Channel;
import com.example.suzumechat.service.channel.ChannelRepository;
import com.example.suzumechat.service.channel.dto.message.VisitorsStatus;
import com.example.suzumechat.service.channel.exception.VisitorNotFoundException;
import com.example.suzumechat.service.guest.Guest;
import com.example.suzumechat.service.guest.GuestRepository;
import com.example.suzumechat.service.guest.dto.ChannelStatus;
import com.example.suzumechat.service.guest.exception.GuestNotFoundException;
import com.example.suzumechat.service.guest.exception.JoinChannelTokenInvalidException;
import com.example.suzumechat.utility.Crypter;
import com.example.suzumechat.utility.Hash;
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

    // DELETE: not used
    @Override
    public List<VisitorsStatus> getVisitorsStatus(final String channelId)
        throws Exception {
        final List<Guest> guests =
            repository.findAllByChannelIdOrderByIdDesc(channelId);

        return toVisitorsStatus(guests, channelId);
    }

    // DELETE:
    private List<VisitorsStatus> toVisitorsStatus(final List<Guest> guests,
        final String channelId) throws Exception {
        return guests.stream().map(guest -> {
            try {
                val visitorId = crypter.decrypt(guest.getVisitorIdEnc(), channelId);
                val codename = crypter.decrypt(guest.getCodenameEnc(), channelId);
                val passphrase =
                    crypter.decrypt(guest.getPassphraseEnc(), channelId);
                val isAuthenticated = guest.getIsAuthenticated();
                return new VisitorsStatus(visitorId, codename, passphrase,
                    isAuthenticated);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }).collect(Collectors.toList());
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
