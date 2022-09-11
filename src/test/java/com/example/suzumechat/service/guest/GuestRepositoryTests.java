package com.example.suzumechat.service.guest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.entity.ChannelFactory;
import com.example.suzumechat.testutil.stub.factory.entity.GuestFactory;

import static org.assertj.core.api.Assertions.*;

import lombok.val;

@SpringJUnitConfig
@DataJpaTest
@Import(TestConfig.class)
public class GuestRepositoryTests {
    @Autowired
    private TestEntityManager db;

    @Autowired
    private GuestRepository repository;

    @Autowired
    private GuestFactory guestFactory;

    @Autowired
    private ChannelFactory channelFactory;

    @Autowired
    private TestRandom random;

    @Test
    public void test_updateStatusByVisitorId_should_update_visitors_authentication_status() {
        val channel = channelFactory.make();
        val guest = guestFactory.isAuthenticated(false).channel(channel).make();
        db.persist(channel);
        db.persist(guest);

        repository.updateIsAuthenticatedByVisitorIdHashed(guest.getVisitorIdHashed(), true);
        db.clear(); // reflect changed to TestEntityManager

        val updated = repository.findByGuestIdHashed(guest.getGuestIdHashed());
        assertThat(updated.getIsAuthenticated()).isTrue();
    }

    // This test is unnecessary, but just checking how Spring JPA works.
    @Test
    public void test_findAllByChannelIdOrderByIdDesc_should_return_list_of_VisitorsStatus() {
        val channelId = random.string.alphanumeric(5);
        for (int i = 0; i < 2; i++) {
            val channel = channelFactory.make();
            val guest = guestFactory.channel(channel).make();
            db.persist(channel);
            db.persist(guest);
        }

        val result = repository.findAllByChannelIdOrderByIdDesc(channelId);

        result.forEach(resultItem -> {
            assertThat(resultItem.getChannelId()).isEqualTo(channelId);
        });
    }

    // This test is unnecessary, but just checking how Spring JPA works.
    @Test
    public void test_findAllByChannelIdOrderByIdDesc_should_return_empty_list_if_not_item_found() {
        val channelId = random.string.alphanumeric();
        val result = repository.findAllByChannelIdOrderByIdDesc(channelId);
        assertThat(result).isEmpty();
    }
}
