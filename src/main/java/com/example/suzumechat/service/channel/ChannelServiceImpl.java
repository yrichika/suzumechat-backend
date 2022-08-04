package com.example.suzumechat.service.channel;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.suzumechat.service.channel.dto.CreatedChannel;
import com.example.suzumechat.service.channel.dto.HostChannel;
import com.example.suzumechat.utility.Crypter;
import com.example.suzumechat.utility.Hash;
import com.example.suzumechat.utility.Random;

import lombok.*;

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
    public CreatedChannel create(String channelName) throws Exception {
        val hostId = UUID.randomUUID().toString();
        val hostIdHashed = hash.digest(hostId);

        val channelId = UUID.randomUUID().toString();
        val ad = channelId;

        val channelTokenId = UUID.randomUUID().toString();
        val channelNameEnc = crypter.encrypt(channelName, ad);

        val hostChannelToken = random.alphanumeric();
        val hostChannelTokenHashed = hash.digest(hostChannelToken);

        val loginChannelToken = random.alphanumeric();
        val loginChannelTokenHashed = hash.digest(loginChannelToken);
        val loginChannelTokenEnc = crypter.encrypt(loginChannelToken, ad);

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
        channel.setLoginChannelTokenHashed(loginChannelTokenHashed);
        channel.setLoginChannelTokenEnc(loginChannelTokenEnc);
        channel.setGuestChannelTokenHashed(guestChannelTokenHashed);
        channel.setGuestChannelTokenEnc(guestChannelTokenEnc);
        channel.setSecretKeyEnc(secretKeyEnc);

        repository.save(channel);

        val hostChannel = new HostChannel(hostChannelToken, loginChannelToken, secretKey);
        return new CreatedChannel(hostId, hostChannel);
    }

    @Override
    public List<Channel> getItemsOrderThan(Integer hours) {
        val hoursAgo = LocalDateTime.now().minusHours(hours);
        val hoursAgoTimestamp = Timestamp.valueOf(hoursAgo).toInstant();
        val date = Date.from(hoursAgoTimestamp);
        return repository.findAllByCreatedAtBefore(date);
    }

    @Transactional
    @Override
    public void trashSecretKeyByHostChannelToken(String hostChannelToken) {
        val hashed = hash.digest(hostChannelToken);
        val channel = repository.findByHostChannelTokenHashed(hashed);
        channel.setSecretKeyEnc(null);
        repository.save(channel);
    }
}
