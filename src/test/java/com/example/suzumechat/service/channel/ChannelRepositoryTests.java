package com.example.suzumechat.service.channel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.example.suzumechat.service.channel.ChannelRepository;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.entity.ChannelFactory;

import static org.assertj.core.api.Assertions.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import lombok.val;

@SpringJUnitConfig
@DataJpaTest
@Import(TestConfig.class)
public class ChannelRepositoryTests {
    @Autowired
    private TestEntityManager db;
    @Autowired
    private ChannelRepository repository;

    @Autowired
    private ChannelFactory factory;
    @Autowired
    private TestRandom random;

    private Channel testChannel;

    @BeforeEach
    public void setUp() {
        testChannel = factory.make();
        db.persist(testChannel);
    }

    /*
     * Some tests are unnecessary, but I just wanted to experiment with spring data
     * jpa.
     */

    @Test
    public void findByChannelId_should_return_channel_by_channel_id() {
        val result = repository.findByChannelId(testChannel.getChannelId());

        assertThat(result.get().getHostIdHashed())
                .isEqualTo(testChannel.getHostIdHashed());
    }

    @Test
    public void findByChannelId_should_return_null_if_not_found() {
        val result = repository.findByChannelId(random.string.alphanumeric(10));

        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void findByHostId_should_return_channel_by_host_id() {
        val result = repository.findByHostIdHashed(testChannel.getHostIdHashed());

        assertThat(result.get().getChannelId())
                .isEqualTo(testChannel.getChannelId());
    }

    @Test
    public void findAllByCreatedAtBefore_should_return_all_channels_before_specified_hour() {

        // REFACTOR: This is a little confusing test.
        val someHour = random.integer.nextInt(10);
        val hoursAgo = LocalDateTime.now().minusHours(someHour);
        val createdAt = Date.from(Timestamp.valueOf(hoursAgo).toInstant());

        val howMany = (int) random.integer.nextInt(10);
        for (int i = 0; i < howMany; i++) {
            val channel = factory.make();
            // at this point @CreationTimestamp takes effect
            // and you cannot override createdAt timestamp
            db.persist(channel);

            // retrieve saved record
            Optional<Channel> saved = repository.findById(channel.getId());
            // and set createdAt
            saved.orElseThrow().setCreatedAt(createdAt);
            // then update the record
            db.merge(saved.get());
        }

        val result = repository.findAllByCreatedAtBefore(createdAt);

        assertThat(result.size()).isEqualTo(howMany);
    }


    @Test
    public void deleteByChannelIdIn_should_delete_channels_by_given_channel_ids() {

        val channelIds = new ArrayList<String>();
        for (int i = 0; i < 5; i++) {
            val channel = factory.make();
            db.persist(channel);
            channelIds.add(channel.getChannelId());
        }

        // check if all channels exists
        channelIds.forEach(channelId -> {
            val before = repository.findByChannelId(channelId);
            assertThat(before.isPresent()).isTrue();
        });

        repository.deleteByChannelIdIn(channelIds);

        // assert all chennels deleted
        channelIds.forEach(channelId -> {
            val after = repository.findByChannelId(channelId);
            assertThat(after.isEmpty()).isTrue();
        });
    }
}
