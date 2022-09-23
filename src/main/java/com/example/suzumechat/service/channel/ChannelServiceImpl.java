package com.example.suzumechat.service.channel;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.suzumechat.service.channel.dto.CreatedChannel;
import com.example.suzumechat.service.channel.dto.HostChannel;
import com.example.suzumechat.service.channel.dto.VisitorsStatus;
import com.example.suzumechat.service.channel.exception.HostUnauthorizedException;
import com.example.suzumechat.service.guest.Guest;
import com.example.suzumechat.service.guest.GuestRepository;
import com.example.suzumechat.utility.Crypter;
import com.example.suzumechat.utility.Hash;
import com.example.suzumechat.utility.Random;

import lombok.*;

@Service
public class ChannelServiceImpl implements ChannelService {

    @Autowired
    private ChannelRepository repository;
    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private Hash hash;
    @Autowired
    private Crypter crypter;
    @Autowired
    private Random random;

    // ChannelTokensとChannelsのcreateを合体させている
    // 目的に合って動くかはまだ不明
    @Transactional
    @Override
    public CreatedChannel create(final String channelName) throws Exception {
        val hostId = UUID.randomUUID().toString();
        val hostIdHashed = hash.digest(hostId);

        val channelId = UUID.randomUUID().toString();
        val ad = channelId;

        val channelTokenId = UUID.randomUUID().toString();
        val channelNameEnc = crypter.encrypt(channelName, ad);

        val hostChannelToken = random.alphanumeric();
        val hostChannelTokenHashed = hash.digest(hostChannelToken);

        val joinChannelToken = random.alphanumeric();
        val joinChannelTokenHashed = hash.digest(joinChannelToken);
        val joinChannelTokenEnc = crypter.encrypt(joinChannelToken, ad);

        val guestChannelToken = random.alphanumeric();
        val guestChannelTokenHashed = hash.digest(guestChannelToken);
        val guestChannelTokenEnc = crypter.encrypt(guestChannelToken, ad);

        val secretKey = random.alphanumeric(32);
        val secretKeyEnc = crypter.encrypt(secretKey, ad);

        val channel = new Channel();
        channel.setChannelId(channelId);
        channel.setHostIdHashed(hostIdHashed);
        channel.setChannelTokenId(channelTokenId);
        channel.setChannelNameEnc(channelNameEnc);
        channel.setHostChannelTokenHashed(hostChannelTokenHashed);
        channel.setJoinChannelTokenHashed(joinChannelTokenHashed);
        channel.setJoinChannelTokenEnc(joinChannelTokenEnc);
        channel.setGuestChannelTokenHashed(guestChannelTokenHashed);
        channel.setGuestChannelTokenEnc(guestChannelTokenEnc);
        channel.setSecretKeyEnc(secretKeyEnc);

        repository.save(channel);

        val hostChannel =
                new HostChannel(hostChannelToken, joinChannelToken, secretKey);
        return new CreatedChannel(hostId, hostChannel);
    }

    @Override
    public Channel getByHostChannelToken(final String hostId,
            final String hostChannelToken) throws Exception {
        val hostIdHashed = hash.digest(hostId);
        final Optional<Channel> channelOpt =
                repository.findByHostIdHashed(hostIdHashed);

        val channel = channelOpt.orElseThrow(HostUnauthorizedException::new);

        val hostChannelTokenHashed = hash.digest(hostChannelToken);
        if (!channel.getHostChannelTokenHashed().equals(hostChannelTokenHashed)) {
            throw new HostUnauthorizedException();
        }

        return channel;
    }


    @Override
    public List<VisitorsStatus> getVisitorsStatus(final String channelId)
            throws Exception {
        final List<Guest> guests =
                guestRepository.findAllByChannelIdOrderByIdDesc(channelId);

        return toVisitorsStatus(guests, channelId);
    }


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
    @Cacheable(value = "guestChannelToken", key = "#hostId")
    public String getGuestChannelToken(final String hostId,
            final String userSentHostChannelToken) throws Exception {
        val channel = getChannelByHostId(hostId);

        val userSentHostChannelTokenHashed = hash.digest(userSentHostChannelToken);
        if (!userSentHostChannelTokenHashed
                .equals(channel.getHostChannelTokenHashed())) {
            throw new HostUnauthorizedException();
        }

        val guestChannelToken = crypter.decrypt(channel.getGuestChannelTokenEnc(),
                channel.getChannelId());
        return guestChannelToken;
    }


    public Channel getChannelByHostId(final String hostId)
            throws HostUnauthorizedException {
        val hostIdHashed = hash.digest(hostId);
        final Optional<Channel> channelOpt =
                repository.findByHostIdHashed(hostIdHashed);
        val channel = channelOpt.orElseThrow(HostUnauthorizedException::new);

        return channel;
    }


    @Override
    public List<Channel> getItemsOrderThan(final Integer hours) {
        val hoursAgo = LocalDateTime.now().minusHours(hours);
        val hoursAgoTimestamp = Timestamp.valueOf(hoursAgo).toInstant();
        val date = Date.from(hoursAgoTimestamp);
        return repository.findAllByCreatedAtBefore(date);
    }


    @Transactional
    @Override
    public void trashSecretKeyByHostChannelToken(final String hostChannelToken)
            throws Exception {
        val hashed = hash.digest(hostChannelToken);
        final Optional<Channel> channelOpt =
                repository.findByHostChannelTokenHashed(hashed);
        // TODO: create right Exception class
        val channel = channelOpt.orElseThrow(RuntimeException::new);
        channel.setSecretKeyEnc(null);
        repository.save(channel);
    }
}
