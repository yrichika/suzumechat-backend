package com.example.suzumechat.service.channel.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.suzumechat.service.channel.Channel;
import com.example.suzumechat.service.channel.ChannelRepository;
import com.example.suzumechat.service.channel.dto.CreatedChannel;
import com.example.suzumechat.service.channel.dto.HostChannel;
import com.example.suzumechat.service.channel.exception.ChannelNotFoundByHostIdException;
import com.example.suzumechat.service.channel.exception.ChannelNotFoundByTokenException;
import com.example.suzumechat.service.channel.exception.HostChannelTokensMismatchException;
import com.example.suzumechat.service.channel.exception.HostUnauthorizedException;
import com.example.suzumechat.utility.Crypter;
import com.example.suzumechat.utility.Hash;
import com.example.suzumechat.utility.Random;
import lombok.val;

@Service
public class ChannelServiceImpl implements ChannelService {

    @Autowired
    private ChannelRepository repository;

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
        val hostChannelTokenEnc = crypter.encrypt(hostChannelToken, ad);

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
        channel.setHostChannelTokenEnc(hostChannelTokenEnc);
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

        val channel = channelOpt.orElseThrow(ChannelNotFoundByHostIdException::new);

        val hostChannelTokenHashed = hash.digest(hostChannelToken);
        if (!channel.getHostChannelTokenHashed().equals(hostChannelTokenHashed)) {
            throw new HostChannelTokensMismatchException();
        }

        return channel;
    }

    @Override
    @Cacheable(value = "guestChannelToken")
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
    public Channel getByGuestChannelToken(final String guestChannelToken)
        throws Exception {
        val guestChannelTokenHashed = hash.digest(guestChannelToken);
        val channelOpt =
            repository.findByGuestChannelTokenHashed(guestChannelTokenHashed);
        val channel = channelOpt.orElseThrow(ChannelNotFoundByTokenException::new);

        return channel;
    }

    @Override
    public String getChannelNameByGuestChannelToken(final String guestChannelToken)
        throws Exception {
        val channel = getByGuestChannelToken(guestChannelToken);
        val channelName =
            crypter.decrypt(channel.getChannelNameEnc(), channel.getChannelId());
        return channelName;
    }

    @Override
    public Channel getByJoinChannelToken(final String joinChannelToken)
        throws Exception {
        val joinChannelTokenHashed = hash.digest(joinChannelToken);
        final Optional<Channel> channelOpt =
            repository.findByJoinChannelTokenHashed(joinChannelTokenHashed);
        val channel = channelOpt.orElseThrow(ChannelNotFoundByTokenException::new);

        return channel;
    }

    @Override
    public String getHostChannelTokenByJoinChannelToken(
        final String joinChannelToken) throws Exception {
        val channel = getByJoinChannelToken(joinChannelToken);
        val hostChannelToken = crypter.decrypt(channel.getHostChannelTokenEnc(),
            channel.getChannelId());
        return hostChannelToken;
    }

    @Override
    public Channel getByHostChannelToken(final String hostChannelToken)
        throws Exception {
        val hostChannelTokenHashed = hash.digest(hostChannelToken);
        val channelOpt =
            repository.findByHostChannelTokenHashed(hostChannelTokenHashed);
        val channel = channelOpt.orElseThrow(ChannelNotFoundByTokenException::new);

        return channel;
    }

    @Override
    public String getJoinChannelTokenByHostChannelToken(
        final String hostChannelToken) throws Exception {
        val channel = getByHostChannelToken(hostChannelToken);
        val joinChannelToken = crypter.decrypt(channel.getJoinChannelTokenEnc(),
            channel.getChannelId());
        return joinChannelToken;
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
    public void trashSecretKeyByHostChannelToken(final String hostId,
        final String hostChannelToken) throws Exception {
        val channel = getByHostChannelToken(hostId, hostChannelToken);

        channel.setSecretKeyEnc(null);
        repository.save(channel);
    }
}
