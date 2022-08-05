package com.example.suzumechat.service.guest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.example.suzumechat.testconfig.TestConfig;
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
    private GuestFactory factory;

    @Test
    public void test_findByChannelId_should_return_() {
        val guest = factory.make();
        db.persist(guest);

        val result = repository.findByChannelId(guest.getChannelId());
        assertThat(result.getChannelId())
            .isEqualTo(guest.getChannelId());
    }

    @Test
    public void test_updateStatusByVisitorId_should_update_() {
        val guest = factory.isAuthenticated(false).make();
        db.persist(guest);

        repository.updateIsAuthenticatedByVisitorIdHashed(guest.getVisitorIdHashed(), true);
        db.clear(); // reflect changed to TestEntityManager

        val updated = repository.findByGuestIdHashed(guest.getGuestIdHashed());
        assertThat(updated.getIsAuthenticated()).isTrue();
    }
}
