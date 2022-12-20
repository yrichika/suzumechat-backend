package com.example.suzumechat.service.guest;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import com.example.suzumechat.service.channel.Channel;
import com.example.suzumechat.testconfig.TestConfig;
import com.example.suzumechat.testutil.random.TestRandom;
import com.example.suzumechat.testutil.stub.factory.entity.ChannelFactory;
import com.example.suzumechat.testutil.stub.factory.entity.GuestFactory;
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
    public void updateStatusByVisitorId_should_update_visitors_authentication_status() {
        val channel = channelFactory.make();
        val guest = guestFactory.isAuthenticated(false).channel(channel).make();
        db.persist(channel);
        db.persist(guest);

        repository.updateIsAuthenticatedByVisitorIdHashed(guest.getVisitorIdHashed(),
            true);
        db.clear(); // reflect changed to TestEntityManager

        val updated = repository.findByGuestIdHashed(guest.getGuestIdHashed());
        assertThat(updated.get().getIsAuthenticated()).isTrue();
    }

    // This test is unnecessary, but just checking how Spring JPA works.
    @Test
    public void findAllByChannelIdOrderByIdDesc_should_return_list_of_VisitorsStatus() {
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
    public void findAllByChannelIdOrderByIdDesc_should_return_empty_list_if_not_item_found() {
        val channelId = random.string.alphanumeric();
        val result = repository.findAllByChannelIdOrderByIdDesc(channelId);
        assertThat(result).isEmpty();
    }

    @Test
    public void deleteByChannelIds_should_delete_all_records_by_channel_ids() {
        val howMany = random.integer.nextInt(5);
        val channels = new ArrayList<Channel>();
        val guests = new ArrayList<Guest>();
        for (int i = 0; i < howMany; i++) {
            val channel = channelFactory.make();
            db.persist(channel);
            channels.add(channel);
        }
        for (Channel channel : channels) {
            val guest = guestFactory.channelId(channel.getChannelId()).channel(channel).make();
            guests.add(guest);
            db.persist(guest);
        }
        // to compare, creating another guest
        val notDeletingChannel = channelFactory.channelId(random.string.alphanumeric(10)).make();
        val notDeletingGuest =
            guestFactory.channelId(notDeletingChannel.getChannelId()).channel(notDeletingChannel).make();
        db.persist(notDeletingChannel);
        db.persist(notDeletingGuest);
        final List<String> channelIds = channels.stream().map(channel -> channel.getChannelId()).toList();

        guests.forEach(guest -> {
            val guestSaved = repository.findByGuestIdHashed(guest.getGuestIdHashed());
            assertThat(guestSaved.isPresent()).isTrue();
        });
        val makingReallySureItExists = repository.findByGuestIdHashed(notDeletingGuest.getGuestIdHashed());
        assertThat(makingReallySureItExists.isPresent()).isTrue();

        val deletedItemsNum = repository.deleteByChannelIds(channelIds);

        assertThat(deletedItemsNum).isEqualTo(howMany);
        guests.forEach(guest -> {
            val guestSaved = repository.findByGuestIdHashed(guest.getGuestIdHashed());
            assertThat(guestSaved.isEmpty()).isTrue();
        });
        val shouldNotBeDeleted = repository.findByGuestIdHashed(notDeletingGuest.getGuestIdHashed());
        assertThat(shouldNotBeDeleted.isPresent()).isTrue();
    }

    @Test
    public void getPendedVisitorsByChannelId_should_return_visitors_only_with_isAuthenticated_null() {
        val channel = channelFactory.make();
        val guestWithIsAuthenticatedTrue = guestFactory.isAuthenticated(true).channel(channel).make();
        val guestWithIsAuthenticatedFalse = guestFactory.isAuthenticated(false).channel(channel).make();
        db.persist(channel);
        db.persist(guestWithIsAuthenticatedTrue);
        db.persist(guestWithIsAuthenticatedFalse);
        val howManyVisitors = random.integer.between(1, 5);
        for (int i = 0; i < howManyVisitors; i++) {
            val guestWithIsAuthenticatedNull = guestFactory.isAuthenticated(null).channel(channel).make();
            db.persist(guestWithIsAuthenticatedNull);
        }

        val visitors = repository.getPendedVisitorsByChannelId(channel.getChannelId());

        assertThat(visitors.size()).isEqualTo(howManyVisitors);
        visitors.forEach(visitor -> {
            assertThat(visitor.getIsAuthenticated()).isNull();
        });

    }
}
